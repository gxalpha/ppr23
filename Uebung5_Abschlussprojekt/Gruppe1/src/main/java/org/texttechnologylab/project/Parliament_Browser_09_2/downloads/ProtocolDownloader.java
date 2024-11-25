package org.texttechnologylab.project.Parliament_Browser_09_2.downloads;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Utility-Klasse zum Runterladen der Plenarprotokolle
 */
public class ProtocolDownloader {

    /**
     * Lädt Protokolle runter.
     *
     * @param wahlperiode        Wahlperiode. Erlaubte Werte: 19 oder 20.
     * @param bekannteProtokolle Liste schon bekannter Protokolle nach Protokollnummer. Verhindert, dass diese Protokolle erneut runtergeladen werden.
     * @return Liste von InputStreams der Protokolle.
     * @author Stud
     * @deprecated Brauchen wir hoffentlich nicht mehr lange --Stud: Falls das am Ende noch da ist: Meine Schuld. Dieses Design war zwar neat aber auch ein Riesenfehler, es macht wenig Sinn alles gleichzeitig zu laden.
     */
    @Deprecated
    public static List<InputStream> downloadProtocols(int wahlperiode, List<Integer> bekannteProtokolle) throws IOException {
        String baseUrl;
        if (wahlperiode == 19) {
            baseUrl = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/543410-543410?limit=10&noFilterSet=true&offset=";
        } else if (wahlperiode == 20) {
            baseUrl = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/866354-866354?limit=10&noFilterSet=true&offset=";
        } else {
            throw new IllegalArgumentException("Wahlperiode darf nur 19 oder 20 sein.");
        }

        List<InputStream> streams = new ArrayList<>();
        int iterations = 0;
        while (true) {
            Document doc = Jsoup.connect(baseUrl + iterations * 10).get();
            Elements table = doc.getElementsByTag("body").get(0).getElementsByTag("div").get(1).getElementsByTag("table");
            if (table.isEmpty()) {
                break;
            }

            Elements rows = table.get(0).getElementsByTag("tbody").get(0).getElementsByTag("tr");
            rows.forEach(element -> {
                Element row = element.getElementsByTag("td").get(0).getElementsByTag("div").get(0);
                String title = row.getElementsByTag("strong").get(0).html();
                int protocolNumber = Integer.parseInt(Arrays.stream(title.replaceFirst("Plenarprotokoll der ", "").split("\\.")).findFirst().get());

                // Don't re-download existing protocols
                if (bekannteProtokolle.contains(protocolNumber)) {
                    return;
                }

                String link = row.getElementsByTag("a").get(0).attribute("href").getValue();
                String downloadBaseUrl = "https://www.bundestag.de";

                try {
                    streams.add(new BufferedInputStream(new URL(downloadBaseUrl + link).openStream()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            iterations++;
        }

        return streams;
    }

    /**
     * Lädt ein Protokoll herunter.
     *
     * @param rawLink Link-Route ohne Domain
     * @return Protokoll als String
     * @author Stud
     */
    public static String downloadProtocol(String rawLink) throws IOException {
        return new String(new BufferedInputStream(new URL("https://www.bundestag.de" + rawLink).openStream()).readAllBytes());
    }

    /**
     * Lädt alle Links zu Protokollen einer Wahlperiode runter.
     *
     * @param wahlperiode Wahlperiode, für die die Links geladen werden sollen. Erlaubte Werte: 19 oder 20.
     * @return Map von Protokollnummer und zugehöriger Download-Route
     * @throws IOException Wenn Probleme auftreten
     * @author Stud
     */
    public static Map<Integer, String> fetchAllProtocolLinks(int wahlperiode) throws IOException {
        String baseUrl;
        if (wahlperiode == 19) {
            baseUrl = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/543410-543410?limit=10&noFilterSet=true&offset=";
        } else if (wahlperiode == 20) {
            baseUrl = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/866354-866354?limit=10&noFilterSet=true&offset=";
        } else {
            throw new IllegalArgumentException("Wahlperiode darf nur 19 oder 20 sein.");
        }

        Map<Integer, String> protocols = new HashMap<>();
        int iterations = 0;
        while (true) {
            Document doc = Jsoup.connect(baseUrl + iterations * 10).get();
            Elements table = doc.getElementsByTag("body").get(0).getElementsByTag("div").get(1).getElementsByTag("table");
            if (table.isEmpty()) {
                break;
            }

            Elements rows = table.get(0).getElementsByTag("tbody").get(0).getElementsByTag("tr");
            rows.forEach(element -> {
                Element tableElement = element.getElementsByTag("td").get(0).getElementsByTag("div").get(0);
                String title = tableElement.getElementsByTag("strong").get(0).html();

                int protocolNumber = Integer.parseInt(Arrays.stream(title.replaceFirst("Plenarprotokoll der ", "").replaceFirst("Teilprotokoll der ", "").replaceFirst("Protokoll der ", "").split("\\.")).findFirst().get());
                String link = tableElement.getElementsByTag("a").get(0).attribute("href").getValue();

                protocols.put(protocolNumber, link);
            });
            iterations++;
        }
        return protocols;
    }
}
