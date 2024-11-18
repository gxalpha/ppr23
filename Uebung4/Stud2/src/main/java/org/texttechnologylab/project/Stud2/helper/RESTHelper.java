package org.texttechnologylab.project.Stud2.helper;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import freemarker.template.Configuration;
import org.hucompute.textimager.uima.type.VaderSentiment;
import org.texttechnologylab.project.Stud2.data.Abgeordneter;
import org.texttechnologylab.project.Stud2.data.BundestagFactory;
import org.texttechnologylab.project.Stud2.data.Rede;
import org.texttechnologylab.project.Stud2.data.impl.mongoDB.RedeMongoDBImpl;
import org.texttechnologylab.project.Stud2.database.MongoDBConnectionHandler;
import org.texttechnologylab.project.Stud2.exceptions.AbgeordneterNotFoundException;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Definiert Routen für REST
 *
 * @author Stud2
 */
public class RESTHelper {
    private final BundestagFactory factory;
    private final MongoDBConnectionHandler mongoDB;
    private final NLPHelper nlpHelper;
    private List<Tuple<String, String>> fraktionen = new ArrayList<>();

    // Konfiguration für FreeMarker
    public static Configuration configuration = Configuration.getDefaultConfiguration();

    /**
     * Konstruktor für einen REST-Helper
     *
     * @param bundestagFactory die Bundestag-Factory mit den Abgeordneten und (analysierten) Reden
     */
    public RESTHelper(BundestagFactory bundestagFactory, MongoDBConnectionHandler mongoDB, NLPHelper nlpHelper) throws IOException {
        this.factory = bundestagFactory;
        this.mongoDB = mongoDB;
        this.nlpHelper = nlpHelper;
        this.fraktionen = this.setFraktionen();
        init();
    }

    /**
     * @return alle Fraktionen der 20. Wahlperiode mit deren Abkürzungen, die in der Route genutzt werden sollen
     */
    public List<Tuple<String, String>> setFraktionen() {
        List<Tuple<String, String>> result = new ArrayList<>();

        // Kommentar entfernen, um zu schauen, ob die untere Liste an Tupeln tatsächlich korrekt ist:
        //System.out.println(getFactory().listFraktionenWithRedenWP20());

        result.add(new Tuple<>("FDP", "Fraktion der Freien Demokratischen Partei"));
        result.add(new Tuple<>("SPD", "Fraktion der Sozialdemokratischen Partei Deutschlands"));
        result.add(new Tuple<>("AFD", "Alternative für Deutschland"));
        result.add(new Tuple<>("CDU-CSU", "Fraktion der Christlich Demokratischen Union/Christlich - Sozialen Union"));
        result.add(new Tuple<>("LINKE", "Fraktion DIE LINKE."));
        result.add(new Tuple<>("GRUENE", "Fraktion BÜNDNIS 90/DIE GRÜNEN"));
        result.add(new Tuple<>("FLOS", "Fraktionslos"));

        return result;
    }

    /**
     * Definiert ein paar nützliche URIs
     */
    public void init() throws IOException {
        Spark.port(1234);
        Spark.staticFileLocation("/templates/static");
        Spark.init();

        // Pfad für die FTL-Dateien
        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));

        // Route für die Startseite
        Spark.get("/InsightBundestag/startseite", (req, res) -> {
            Map<String, Object> attributes = new HashMap<>();

            attributes.put("author", "Stud2");
            attributes.put("nlpProgress", getNLPHelper().getNLPProgress());

            return new ModelAndView(attributes, "startseite.ftl");
        }, new FreeMarkerEngine(configuration));

        // Route für das Stammdatenblatt eines Abgeordneten bei gegebener ID
        Spark.get("/InsightBundestag/abgeordnete/:id", (req, res) -> {

            // Abgeordneten aus der Factory holen
            Abgeordneter abgeordneter = getFactory().getAbgeordneterByIDFromMongoDB(Integer.parseInt(req.params(":id")));

            // Gehaltene Reden aus der Factory holen
            List<Rede> reden = new ArrayList<>();
            for (String rede : abgeordneter.getRedenIDs()) {
                reden.add(getFactory().getRedeByIDFromMongoDB(Integer.parseInt(rede)));
            }

            Map<String, Object> attributes = new HashMap<>();

            attributes.put("partei", abgeordneter.getPartei().getLabel());
            attributes.put("vorname", abgeordneter.getVorname());
            attributes.put("nachname", abgeordneter.getName());
            attributes.put("anrede", abgeordneter.getAnrede());
            attributes.put("akadTitel", abgeordneter.getAkadTitel());
            attributes.put("geburtsdatum", abgeordneter.getGeburtsDatum());
            attributes.put("geburtsort", abgeordneter.getGeburtsOrt());
            attributes.put("sterbedatum", abgeordneter.getSterbeDatum());
            attributes.put("geschlecht", abgeordneter.getGeschlecht().toString());
            attributes.put("religion", abgeordneter.getReligion());
            attributes.put("beruf", abgeordneter.getBeruf());
            attributes.put("mandate", abgeordneter.listMandate());
            attributes.put("reden", reden);
            attributes.put("vita", abgeordneter.getVita());

            return new ModelAndView(attributes, "stammdatenblatt.ftl");
        }, new FreeMarkerEngine(configuration));

        // Route, die checkt, ob der gesuchte Abgeordnete (nach Name) existiert und leitet dich entsprechend weiter.
        Spark.get("/InsightBundestag/sucheAbgeordneterNachName", (req, res) -> {

            Abgeordneter abgeordneter;
            try {
                abgeordneter = getFactory().getAbgeordneterByNameFromMongoDB(req.queryParams("nachname"), req.queryParams("vorname"));
            } catch (AbgeordneterNotFoundException e) {
                abgeordneter = null;
            }

            if (req.queryParams("vorname").isEmpty() || req.queryParams("nachname").isEmpty()) {
                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Search failed, because one field was left empty.");
                res.redirect("/InsightBundestag/startseite");
            } else if (abgeordneter == null) {
                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Search for "
                        + req.queryParams("vorname").substring(0, 1).toUpperCase() + req.queryParams("vorname").substring(1) + " "
                        + req.queryParams("nachname").substring(0, 1).toUpperCase() + req.queryParams("nachname").substring(1)
                        + " failed.");
                res.redirect("/InsightBundestag/startseite");
            } else {
                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Search for "
                        + req.queryParams("vorname").substring(0, 1).toUpperCase() + req.queryParams("vorname").substring(1) + " "
                        + req.queryParams("nachname").substring(0, 1).toUpperCase() + req.queryParams("nachname").substring(1)
                        + " successful.");

                res.redirect("/InsightBundestag/abgeordnete/" + abgeordneter.getID());
            }

            return null;
        });

        // Route, die checkt, ob der gesuchte Abgeordnete (nach ID) existiert und leitet dich entsprechend weiter.
        Spark.get("/InsightBundestag/sucheAbgeordneterNachId", (req, res) -> {

            Abgeordneter abgeordneter;
            try {
                abgeordneter = getFactory().getAbgeordneterByIDFromMongoDB(Integer.parseInt(req.queryParams("id")));
            } catch (Exception e) {
                abgeordneter = null;
            }

            if (req.queryParams("id").isEmpty()) {
                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Search failed, because field was left empty.");
                res.redirect("/InsightBundestag/startseite");
            } else if (abgeordneter == null) {
                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Search for Abgeordneter with ID "
                        + req.queryParams("id") + " failed.");
                res.redirect("/InsightBundestag/startseite");
            } else {
                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Search for Abgeordneter with ID"
                        + req.queryParams("id") + " successful.");

                res.redirect("/InsightBundestag/abgeordnete/" + abgeordneter.getID());
            }

            return null;
        });

        // Route, die checkt, ob die gesuchte Rede (nach ID) existiert und leitet dich entsprechend weiter.
        Spark.get("/InsightBundestag/sucheRedeNachId", (req, res) -> {

            Rede rede;
            try {
                rede = getFactory().getRedeByIDFromMongoDB(Integer.parseInt(req.queryParams("id")));
            } catch (Exception e) {
                rede = null;
            }

            if (req.queryParams("id").isEmpty()) {
                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Search failed, because field was left empty.");
                res.redirect("/InsightBundestag/startseite");
            } else if (rede == null) {
                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Search for Speech with ID "
                        + req.queryParams("id") + " failed.");
                res.redirect("/InsightBundestag/startseite");
            } else {
                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Search for Speech with ID"
                        + req.queryParams("id") + " successful.");

                res.redirect("/InsightBundestag/reden/" + rede.getID());
            }

            return null;
        });

        // Route, die checkt, ob der eingegebene Zeitraum für die Suche nach Reden gültig ist und dich entsprechend weiterleitet.
        Spark.get("/InsightBundestag/sucheRedenNachZeitraum", (req, res) -> {

            try {
                // Checken, ob Eingaben dem geforderten Format entsprechen
                Date von = StringHelper.toDate(req.queryParams("start"));
                Date bis = StringHelper.toDate(req.queryParams("ende"));

                // Das Anfangsdatum muss vor dem Enddatum liegen und das Anfangsdatum darf nicht in der Zukunft liegen
                assert bis != null;
                assert von != null;
                if (von.before(bis) && von.before(new Date(new java.util.Date().getTime()))) {

                    res.redirect("/InsightBundestag/reden/" + req.queryParams("start") + "/" + req.queryParams("ende"));
                } else {
                    System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] The end date lies before the start date or the start date lies in the future! Search for speeches failed.");
                    res.redirect("/InsightBundestag/startseite");
                }
            } catch (ParseException e) {
                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] An entered date does not have the format dd.MM.YYYY. Search for speeches failed.");
                res.redirect("/InsightBundestag/startseite");
            }

            return null;
        });

        // Route für die Darstellung einer Liste der gefundenen Reden, die nach einem Zeitraum gefiltert wurden
        Spark.get("/InsightBundestag/reden/:start/:ende", (req, res) -> {

            Date von = StringHelper.toDate(req.params(":start"));
            Date bis = StringHelper.toDate(req.params(":ende"));

            // Das Anfangsdatum muss vor dem Enddatum liegen und das Anfangsdatum darf nicht in der Zukunft liegen
            assert bis != null;
            assert von != null;

            // Gehaltene Reden aus der Factory holen;
            List<Rede> reden = new ArrayList<>();
            for (Rede rede : this.getFactory().getRedenDB().values()) {
                if (rede.getDate().before(bis) && rede.getDate().after(von)) {
                    reden.add(rede);
                }
            }

            reden.sort(Comparator.comparing(o -> (int) o.getID()));

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("reden", reden);
            attributes.put("anzahlErgebnisse", reden.size());
            attributes.put("start", req.params(":start"));
            attributes.put("ende", req.params(":ende"));

            return new ModelAndView(attributes, "suchergebnisseReden.ftl");
        }, new FreeMarkerEngine(configuration));

        // Route für die Darstellung einer Rede
        Spark.get("/InsightBundestag/reden/:id", (req, res) -> {

            RedeMongoDBImpl rede = (RedeMongoDBImpl) getFactory().getRedeByIDFromMongoDB(Integer.parseInt(req.params(":id")));

            // Es gab eine Rede, die nicht in der MongoDB hochgeladen werden konnte
            if ((int) rede.getID() == 20800100) {
                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] The speech " + rede.getID() + " is the only speech that does not have a CAS-Attribute because MongoDB is stupid.");
                res.redirect("/InsightBundestag/startseite");
                return null;
            }

            System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Opened analysis of speech " + req.params(":id") + ".");

            rede.fetchCASFromMongoDB(this.getMongoDBConnectionHandler());

            Map<String, Object> attributes = new HashMap<>();

            attributes.put("id", rede.getID().toString());
            attributes.put("datum", rede.getDate());
            attributes.put("rednerID", rede.getRedner().getID());
            attributes.put("rednerVorname", rede.getRedner().getVorname());
            attributes.put("rednerNachname", rede.getRedner().getName());
            attributes.put("rednerPartei", rede.getRedner().getPartei().getLabel());

            attributes.put("token", rede.getToken());
            attributes.put("sentences", rede.getSentences());
            attributes.put("pos", rede.getPOS());
            attributes.put("dependency", rede.getDependency());
            attributes.put("namedEntities", rede.getNamedEntities());
            attributes.put("sentiment", rede.getSentiment());

            double positive = rede.getSentiment().get(0).getPos();
            double neutral = rede.getSentiment().get(0).getNeu();
            double negative = rede.getSentiment().get(0).getNeg();

            DecimalFormat decimalFormat = new DecimalFormat("0.000");

            attributes.put("overallSentiment", decimalFormat.format(rede.getSentiment().get(0).getSentiment()));
            attributes.put("positive", decimalFormat.format(positive / (positive + neutral + negative)));
            attributes.put("neutral", decimalFormat.format(neutral / (positive + neutral + negative)));
            attributes.put("negative", decimalFormat.format(negative / (positive + neutral + negative)));

            // Für die Darstellung des Sentiments der einzelnen Sätze:
            List<VaderSentiment> sentimentSentences = rede.getSentiment().subList(1, rede.getSentiment().size());

            attributes.put("sentimentSentences", sentimentSentences);

            // Für die Darstellung der Named Entities und Nomen über die POS-Objekte
            List<Tuple<String, String>> posText = new ArrayList<>();

            List<POS> posList = rede.getPOS();

            for (int i = 0; i < rede.getPOS().size() - 1; i++) {
                POS curr = posList.get(i);

                posText.add(new Tuple<>(curr.getCoveredText(), curr.getPosValue()));
            }

            attributes.put("posText", posText);

            rede.setCasToNull(); // Speicher sparen :)

            return new ModelAndView(attributes, "rede.ftl");
        }, new FreeMarkerEngine(configuration));

        // Route für den Start des NLP aller Reden
        Spark.get("/InsightBundestag/nlp/start", (req, res) -> {

            System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Started NLP of all speeches!");

            new Thread(() -> {
                try {
                    List<RedeMongoDBImpl> reden = new ArrayList<>(this.getFactory().getRedenDB().values());
                    this.getNLPHelper().analyse(reden);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            return "NLP started. Return to the page before, please.";
        });

        // Route für den Abbruch des NLP
        Spark.get("/InsightBundestag/nlp/stop", (req, res) -> {
            System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] User stopped the NLP. All other remaining speeches will not be reanalysed.");
            this.getNLPHelper().setContinueNLP(false);
            this.getNLPHelper().setNLPProgress("0.00%");
            return null;
        });

        // Route für den aktuellen NLP-Fortschritt
        Spark.get("/InsightBundestag/nlp/progress", (req, res) -> nlpHelper.getNLPProgress());

        // Route für die vergangene Zeit seit dem Start des NLP
        Spark.get("/InsightBundestag/nlp/progress/passedTime", (req, res) -> StringHelper.getPrettyDuration(nlpHelper.getStartTimeNLP(), new java.util.Date().getTime()));

        // Route, die die Eingaben bei der Fraktionssuche nach Gültigkeit überprüft und dann entsprechend weiterleitet
        Spark.get("/InsightBundestag/fraktionen", (req, res) -> {
            boolean fraktionExists = false;
            String fraktionAbbr = "";

            // Checken, ob Fraktion existiert
            for (Tuple<String, String> tuple : this.fraktionen) {
                if (tuple.getFirst().equals(req.queryParams("fraktionsname"))) {
                    fraktionExists = true;
                    fraktionAbbr = tuple.getFirst();
                }
            }

            if (fraktionExists) {
                try {
                    // Checken, ob Eingaben dem geforderten Format entsprechen
                    Date von = StringHelper.toDate(req.queryParams("start"));
                    Date bis = StringHelper.toDate(req.queryParams("ende"));

                    // Das Anfangsdatum muss vor dem Enddatum liegen und das Anfangsdatum darf nicht in der Zukunft liegen
                    assert bis != null;
                    assert von != null;
                    if (von.before(bis) && von.before(new Date(new java.util.Date().getTime()))) {

                        res.redirect("/InsightBundestag/fraktionen/" + fraktionAbbr + "/sentiment/" + req.queryParams("start") + "/" + req.queryParams("ende"));
                    } else {
                        System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] The end date lies before the start date or the start date lies in the future! Calculation of avg. Sentiment failed.");
                        res.redirect("/InsightBundestag/startseite");
                    }

                } catch (ParseException e) {
                    System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] An entered date does not have the format dd.MM.YYYY. Calculation of avg. Sentiment failed.");
                    res.redirect("/InsightBundestag/startseite");
                }
            } else {
                System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Search for " + req.queryParams("fraktionsname") + " failed.");
            }
            return null;
        });

        // Route für die durchschnittlichen Sentiment-Werte einer Fraktion
        Spark.get("/InsightBundestag/fraktionen/:name/sentiment/:von/:bis", (req, res) -> {

            // Vollständigen Fraktionsnamen bestimmen
            String fullName = null;
            for (Tuple<String, String> tuple : this.fraktionen) {
                if (tuple.getFirst().equals(req.params(":name"))) {
                    fullName = tuple.getSecond();
                }
            }
            System.out.println(StringHelper.getCurrDateTimeFormatted() + " [INFORMATION] Calculating the average sentiment values for "
                    + fullName + " from " + req.params(":von") + " to " + req.params(":bis") + " ...");

            // Durchschnittliche Sentiment-Werte berechnen
            Map<String, Double> results = nlpHelper.getAverageSentimentValues(fullName, req.params(":von"), req.params(":bis"));

            DecimalFormat decimalFormat = new DecimalFormat("0.000");

            Map<String, Object> attributes = new HashMap<>();

            attributes.put("fraktion", fullName);
            attributes.put("von", req.params(":von"));
            attributes.put("bis", req.params(":bis"));
            attributes.put("anzahlReden", results.get("anzahlReden") != null ? results.get("anzahlReden").intValue() : 0);
            attributes.put("avgSentiment", results.get("anzahlReden") != null ? decimalFormat.format((double) results.get("avgSentiment")) : "k. A.");
            attributes.put("avgPositive", results.get("anzahlReden") != null ? decimalFormat.format((double) results.get("avgPositive")) : "k. A.");
            attributes.put("avgNeutral", results.get("anzahlReden") != null ? decimalFormat.format((double) results.get("avgNeutral")) : "k. A.");
            attributes.put("avgNegative", results.get("anzahlReden") != null ? decimalFormat.format((double) results.get("avgNegative")) : "k. A.");

            return new ModelAndView(attributes, "sentimentFraktion.ftl");
        }, new FreeMarkerEngine(configuration));
    }

    /**
     * @return die Bundestag-Factory
     */
    public BundestagFactory getFactory() {
        return this.factory;
    }

    /**
     * @return die MongoDB
     */
    public MongoDBConnectionHandler getMongoDBConnectionHandler() {
        return this.mongoDB;
    }

    /**
     * @return den NLP-Helper
     */
    public NLPHelper getNLPHelper() {
        return this.nlpHelper;
    }
}
