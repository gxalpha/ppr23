package org.texttechnologylab.project.Parliament_Browser_09_2.downloads;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.AbgeordneterFotos;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.impl.AbgeordneterFotosImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasse, die die Bilder eines Abgeordneten aus der Bilddatenbank herunter läd.
 *
 * @author Stud
 */
public class PictureDownloader {


    /**
     * Läd die ersten 20 Bilder eines Abgeordneten aus der Bilddatenbank herunter.
     *
     * @param vorname  Vorname des Abgeordneten
     * @param nachname Nachname des Abgeordneten
     * @return Liste aller AbgeordnetenFotos Objekte
     * @author Stud
     */
    public static List<AbgeordneterFotos> downloadPictures(String vorname, String nachname) {

        List<AbgeordneterFotos> result = new ArrayList<>();

        String url = "https://bilddatenbank.bundestag.de/search/picture-result?query=" + vorname + "+" + nachname + "&sortVal=2";

        try {

            // Connecte zur Website der Bilddatenbank und lade Bilder herunter
            org.jsoup.nodes.Document doc = Jsoup.connect(url).get();

            Elements tagsWithDataFancybox = doc.select("[data-fancybox=group]");

            // Für jedes Bild hole Metadaten und erstelle AbgeordneterFotos-Objekt
            for (Element coreTag : tagsWithDataFancybox) {

                // Hole Name des Fotograf/in
                String ariaLabel = coreTag.attr("aria-label");

                int index = ariaLabel.indexOf("Fotograf/in:");

                String photographer;

                if (index != -1) {
                    photographer = ariaLabel.substring(index + 13).trim();
                } else {
                    photographer = "Unknown Photographer";
                }

                // Hole ID des Bildes
                String id = coreTag.attr("href").substring(coreTag.attr("href").indexOf("id=") + 3);

                // Hole Beschreibung des Bildes
                Element imgElement = coreTag.select("img[src~=(?i)\\.(jpg)]").first();
                String label = imgElement.attr("title");
                String alt = imgElement.attr("alt");

                // Get the source URL of the image

                String url_string = "https://bilddatenbank.bundestag.de" + imgElement.attr("src");

                AbgeordneterFotos foto = new AbgeordneterFotosImpl(id, label, alt, photographer, url_string, false);
                result.add(foto);

            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;

    }

}
