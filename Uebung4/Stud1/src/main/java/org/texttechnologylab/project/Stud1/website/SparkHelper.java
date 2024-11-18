package org.texttechnologylab.project.Stud1.website;

import freemarker.template.Configuration;
import org.bson.Document;
import org.texttechnologylab.project.Stud1.data.Abgeordneter;
import org.texttechnologylab.project.Stud1.data.Rede;
import org.texttechnologylab.project.Stud1.data.RedeNLP;
import org.texttechnologylab.project.Stud1.data.impl.RedeNLP_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.Rede_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.database.MongoDBConnectionHandler;
import org.texttechnologylab.project.Stud1.nlp.DUUIHelper;
import org.texttechnologylab.project.Stud1.nlp.NLPAnalyzer;
import org.texttechnologylab.project.Stud1.util.Static;
import spark.ModelAndView;
import spark.Response;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.Spark.get;

/**
 * Klasse zum Starten und Managen des Webservers
 */
public class SparkHelper {
    public static Map<String, String> fraktionenColorMap;
    private static Map<String, String> fraktionenMap;

    /**
     * Versteckter Konstruktor
     */
    private SparkHelper() {
    }

    /**
     * Startet den Webserver
     *
     * @param handler MongoDBConnectionHandler zum Zugreifen auf die Datenbank
     */
    public static void startup(MongoDBConnectionHandler handler) {
        initFraktionenMap();

        Spark.port(8001);
        Spark.staticFiles.location("spark_static");
        Spark.init();

        Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        config.setClassForTemplateLoading(SparkHelper.class, "/freemarker_templates");
        FreeMarkerEngine engine = new FreeMarkerEngine(config);

        Logger logger = new Logger(handler);

        get("/", (request, response) -> {
            Map<String, String> attributes = new HashMap<>();

            if (Math.random() < 0.5) {
                attributes.put("app_name", "Inside Bundestag");
            } else {
                attributes.put("app_name", "InsightBundestag");
            }

            return new ModelAndView(attributes, "main.ftl");
        }, engine);

        get("/abgeordneter", (request, response) -> {
            String id = request.queryParams("id");
            if (id == null) {
                return makeErrorPage(response, 400, "Bad Request", "Erforderlicher Parameter \"id\" wurde nicht angegeben.");
            }

            Abgeordneter abgeordneter = handler.getAbgeordneterByID(id);
            if (abgeordneter == null) {
                return makeErrorPage(response, 404, "Not Found", "Der Abgeordnete mit der ID \"" + id + "\" konnte nicht gefunden werden.");
            }

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("abgeordneter", abgeordneter);
            response.status(200);
            return new ModelAndView(attributes, "abgeordneter.ftl");
        }, engine);

        get("/abgeordneter/search", (request, response) -> {
            String vorname = request.queryParams("vorname");
            String nachname = request.queryParams("nachname");

            if (vorname == null || nachname == null) {
                return makeErrorPage(response, 400, "Bad Request", "Erforderlicher Parameter \"vorname\" oder \"nachname\" wurde nicht angegeben.");
            }

            List<Abgeordneter> abgeordnete = handler.getAbgeordneteByName(vorname, nachname);

            String queryString;
            if (!vorname.isEmpty() || !nachname.isEmpty()) {
                queryString = vorname + " " + nachname;
            } else {
                queryString = "alle Abgeordneten";
            }

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("abgeordnete", abgeordnete);
            attributes.put("searchQuery", queryString);
            return new ModelAndView(attributes, "abgeordneter_suche.ftl");
        }, engine);

        get("/rede", (request, response) -> {
            String id = request.queryParams("id");
            if (id == null) {
                return makeErrorPage(response, 400, "Bad Request", "Erforderlicher Parameter \"id\" wurde nicht angegeben.");
            }

            Document redeDoc = handler.getRedeByID(id);
            if (redeDoc == null) {
                return makeErrorPage(response, 404, "Not Found", "Die Rede mit der ID \"" + id + "\" konnte nicht gefunden werden.");
            }
            Rede rede = new Rede_MongoDB_Impl(redeDoc);
            Document nlpDoc = (Document) redeDoc.get(Rede_MongoDB_Impl.Keys.NLP);

            Map<String, Object> attributes = new HashMap<>();
            if (nlpDoc != null) {
                RedeNLP nlp = new RedeNLP_MongoDB_Impl(nlpDoc);
                attributes.put("nlp", nlp);
            }

            Abgeordneter redner = handler.getAbgeordneterByID(redeDoc.getString(Rede_MongoDB_Impl.Keys.REDNER));
            if (redner == null) {
                return makeErrorPage(response, 418, "I'm a teapot", "Die Rede wurde gefunden, aber der zugehörige Redner nicht. Das sollte nicht passieren.");
            }

            attributes.put("rede", rede);
            attributes.put("redner", redner);
            return new ModelAndView(attributes, nlpDoc != null ? "rede.ftl" : "rede_blank.ftl");
        }, engine);

        get("/reden", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            Date from = null;
            Date until = null;
            try {
                from = Date.valueOf(request.queryParams("from"));
            } catch (IllegalArgumentException ignored) {
            }
            try {
                until = Date.valueOf(request.queryParams("until"));
            } catch (IllegalArgumentException ignored) {
            }
            String timespan;
            if (from != null && until != null) {
                timespan = "Von " + from + " bis " + until;
            } else if (from != null) {
                timespan = "Ab " + from;
            } else if (until != null) {
                timespan = "Bis " + until;
            } else {
                timespan = "Immer";
            }

            List<String> sentimentJsons = handler.getRedeSentimentsJson(from, until);
            attributes.put("sentimentInfos", sentimentJsons);
            attributes.put("abkuerzungen", fraktionenMap.keySet().stream().filter(key -> !key.equals(fraktionenMap.get(key))).map(key -> fraktionenMap.get(key) + ": " + key).collect(Collectors.toList()));
            attributes.put("timespan", timespan);

            return new ModelAndView(attributes, "reden_overview.ftl");
        }, engine);

        get("/nlp", (request, response) -> new ModelAndView(Map.of("isDeployment", !Static.ENABLE_NLP), "nlp.ftl"), engine);
        get("/nlp/readyStatus", (request, response) -> NLPAnalyzer.isReady() && DUUIHelper.isReady() ? "ready" : "notReady");
        get("/nlp/runningStatus", (request, response) -> NLPAnalyzer.isRunning() ? "running" : "idle");
        get("/nlp/progress", (request, response) -> NLPAnalyzer.getProgress());
        get("/nlp/start", (request, response) -> {
            logger.log("/nlp/start", Map.of());

            if (!NLPAnalyzer.isReady() || !DUUIHelper.isReady()) {
                response.status(400);
                return "400 Bad Request - NLP is not yet ready.";
            }
            NLPAnalyzer.start(handler);
            response.status(200);
            return "200 Ok";
        });
        get("/nlp/stop", (request, response) -> {
            logger.log("/nlp/stop", Map.of());

            if (!NLPAnalyzer.isReady() || !DUUIHelper.isReady()) {
                response.status(400);
                return "400 Bad Request - NLP is not yet ready.";
            }
            NLPAnalyzer.stop();
            response.status(200);
            return "200 Ok";
        });

        get("/log", ((request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("logEntries", logger.getEntries());
            return new ModelAndView(attributes, "log.ftl");
        }), engine);

    }

    /**
     * Helper function to create an error page
     *
     * @param response       Response object
     * @param code           HTTP status code
     * @param reason         HTTP status code description
     * @param detailedReason Human-readable description for what went wrong
     * @return A new page showing the error
     */
    private static ModelAndView makeErrorPage(Response response, int code, String reason, String detailedReason) {
        response.status(code);
        String status = code + " " + reason;
        return new ModelAndView(Map.of("status", status, "reason", detailedReason), "error.ftl");
    }

    /**
     * Initialisiert die fraktionenMap für shortenFraktion()
     */
    private static void initFraktionenMap() {
        fraktionenMap = new HashMap<>();
        fraktionenMap.put("Fraktionslos", "Fraktionslos");
        fraktionenMap.put("Fraktion der Freien Demokratischen Partei", "FDP");
        fraktionenMap.put("Alternative für Deutschland", "AfD");
        fraktionenMap.put("Fraktion DIE LINKE.", "LINKE");
        fraktionenMap.put("Fraktion der Sozialdemokratischen Partei Deutschlands", "SPD");
        fraktionenMap.put("Fraktion der Christlich Demokratischen Union/Christlich - Sozialen Union", "CDU/CSU");
        fraktionenMap.put("Fraktion BÜNDNIS 90/DIE GRÜNEN", "GRÜNE");
        fraktionenColorMap = new HashMap<>();
        fraktionenColorMap.put("Fraktionslos", "white");
        fraktionenColorMap.put("FDP", "#ffed00");
        fraktionenColorMap.put("AfD", "#734b17");
        fraktionenColorMap.put("LINKE", "#8100A1");
        fraktionenColorMap.put("SPD", "#E3000F");
        fraktionenColorMap.put("CDU/CSU", "#000000");
        fraktionenColorMap.put("GRÜNE", "#46962b");
    }

    /**
     * Kürzt einen Fraktionsnamen zu einem bekannten Kürzel,
     * oder erstellt einen kurzen Platzhalter, falls kein Kürzel
     * bekannt.
     *
     * @param fraktion Langer Fraktionsname
     * @return Kurzer Fraktionsname
     */
    public static String shortenFraktion(String fraktion) {
        if (!fraktionenMap.containsKey(fraktion)) {
            fraktionenMap.put(fraktion, "andere_" + fraktionenMap.size());
        }
        return fraktionenMap.get(fraktion);
    }

}
