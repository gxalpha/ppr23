package org.texttechnologylab.project.Parliament_Browser_09_2.data.impl;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElement;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.MongoDBHandlerImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Eine Bundestagsrede.
 *
 * @author Stud
 */
public class RedeImpl implements Rede {
    private final String id;
    private final String rednerID;
    private final Date datum;
    private final List<RedeElement> redeElemente;
    private String topic = null;
    private double sentiment;
    private double positiv;
    private double negativ;
    private double neutral;

    /**
     * Konstruktor
     *
     * @param id           ID der Rede
     * @param rednerID     ID des Redners
     * @param datum        Datum
     * @param redeElemente Rede-Elemente
     */
    public RedeImpl(String id, String rednerID, Date datum, List<RedeElement> redeElemente) {
        this.id = id;
        this.rednerID = rednerID;
        this.datum = datum;
        this.redeElemente = redeElemente;
        this.sentiment = 0;
        this.positiv = 0;
        this.negativ = 0;
        this.neutral = 0;
    }

    /**
     * Konstruktor aus der Datenbank
     *
     * @param document aus der Datenbank
     * @author Stud
     * @author Stud (Änderungen)
     */
    public RedeImpl(Document document) {
        this.id = document.getString("_id");
        this.rednerID = document.getString("rednerID");
        this.datum = document.getDate("datum");
        this.redeElemente = document.getList("redeElemente", Document.class)
                .stream()
                .map(RedeElementImpl::new)
                .collect(Collectors.toList());
        this.sentiment = document.getDouble("sentiment");
        this.positiv = document.getDouble("positiv");
        this.negativ = document.getDouble("negativ");
        this.neutral = document.getDouble("neutral");
    }

    /**
     * @return ID der Rede
     */
    @Override
    public String getID() {
        return this.id;
    }

    /**
     * @return ID des Redners
     */
    @Override
    public String getRednerID() {
        return this.rednerID;
    }

    /**
     * @return Datum der Rede
     */
    @Override
    public Date getDatum() {
        return this.datum;
    }

    /**
     * @return Text der Rede. Kommentare und Präsidium zählen nicht zum Text
     * @author Stud
     */
    @Override
    public String getText() {
        return this.redeElemente.stream()
                .filter(re -> re.getTyp() == RedeElement.RedeElementTyp.ABSATZ)
                .map(RedeElement::getText)
                .collect(Collectors.joining(" "));
    }


    /**
     * Methode um den Text einer Rede als Liste von Strings zu erhalten.
     *
     * @return Text der Rede als Liste von Strings.
     * @author Stud
     */
    @Override
    public List<String> getTextByElement() {

        List<String> result = new ArrayList<>();

        for (RedeElement redeElement : this.redeElemente) {
            String text = redeElement.getText();
            result.add(text);
        }

        return result;

    }


    /**
     * @return Topic der Rede
     */
    @Override
    public String getTopic() {
        return this.topic;
    }

    /**
     * Setzt das Topic der Rede
     *
     * @author Stud
     */
    @Override
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * @return Gesamtsentiment der Rede
     * @author Stud
     */
    @Override
    public double getSentiment() {
        return sentiment;
    }

    /**
     * Setzt das Gesamtsentiment der Rede
     *
     * @param sentiment Das Sentiment
     * @author Stud
     */
    @Override
    public void setSentiment(double sentiment) {
        this.sentiment = sentiment;
    }

    /**
     * @return Positiver Wert der Rede
     * @author Stud
     */
    @Override
    public double getPositiv() {
        return this.positiv;
    }

    /**
     * Setzt das Positiven Wert der Rede
     *
     * @param positiv Positiver Wert
     * @author Stud
     */
    @Override
    public void setPositiv(double positiv) {
        this.positiv = positiv;
    }

    /**
     * @return Ngeativer Wert der Rede
     * @author Stud
     */
    @Override
    public double getNegativ() {
        return this.negativ;
    }

    /**
     * Setzt den neagtiven Wert der Rede
     *
     * @param negativ Das Sentiment
     * @author Stud
     */
    @Override
    public void setNegativ(double negativ) {
        this.negativ = negativ;
    }

    /**
     * @return neutraler Wert der Rede
     * @author Stud
     */
    @Override
    public double getNeutral() {
        return this.neutral;
    }

    /**
     * Setzt den neutralen Wert der Rede
     *
     * @param neutral neutraler Wert
     * @author Stud
     */
    @Override
    public void setNeutral(double neutral) {
        this.negativ = neutral;
    }

    /**
     * @return Rede-Elemente der Rede
     */
    @Override
    public List<RedeElement> getRedeElemente() {
        return this.redeElemente;
    }

    /**
     * @return CAS-Repräsentation der Rede. Kommentare und Präsidium zählen nicht zum Text
     */
    @Override
    public JCas toCAS() throws UIMAException {
        return JCasFactory.createText(this.getText(), "de");
    }

    /**
     * @return Objekt in ihrer MongoDB-Repräsentation
     */
    @Override
    public Document toDoc() {
        Document document = new Document();
        document.append("_id", this.getID());
        document.append("rednerID", this.getRednerID());
        document.append("datum", this.getDatum());
        document.append("topic", this.getTopic());
        document.append("sentiment", this.getSentiment());
        document.append("positiv", this.getPositiv());
        document.append("negativ", this.getNegativ());
        document.append("neutral", this.getNeutral());
        document.append("volltext", this.getText());

        document.put("redeElemente", this.redeElemente.stream()
                .map(RedeElement::toDoc)
                .collect(Collectors.toList()));
        return document;
    }

    /**
     * Gesamte Rede als LaTeX-Dokument
     *
     * @return String mit LaTeX-Code
     * @author Stud
     */
    @Override
    public String toLaTeX() throws IOException {
        String texCode = "";

        texCode += "\\subsection{Rede " + this.id + "}\n\n";

        texCode += "\\subsubsection{Redner}\n\n";

        MongoDBHandler mongoDBHandler = new MongoDBHandlerImpl("Project_09_02.txt");
        Abgeordneter redner = mongoDBHandler.getAbgeordneterByID(this.rednerID);

        texCode += redner.getNameFormatted() + "\n";

        String imageUrl = mongoDBHandler.getImage(rednerID);

        int lastIndex = imageUrl.lastIndexOf('/');

        // Alles ab dem letzten Schrägstrich extrahieren
        String filename = imageUrl.substring(lastIndex + 1);

        String image = filename;
        String destinationFolder = "tmp/latex/";
        File destinationFile = new File(destinationFolder + image);

        if (!destinationFile.exists()) {
            try {
                URL url = new URL(imageUrl);
                InputStream inputStream = url.openStream();

                Path folderPath = Paths.get(destinationFolder);
                Files.createDirectories(folderPath);

                Files.copy(inputStream, destinationFile.toPath());
                inputStream.close();
                System.out.println("Bild wurde erfolgreich heruntergeladen und lokal gespeichert.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Fehler beim Herunterladen des Bildes: " + e.getMessage());
            }
        }

        texCode += "\\begin{figure}[H]\n";
        texCode += "\\includegraphics[width=5cm]{" + image + "}\n";
        texCode += "\\centering\n";
        texCode += "\\end{figure}\n\n";

        texCode += "\\subsubsection{Inhalt}\n\n";

        for (RedeElement element : this.redeElemente) {
            texCode += element.toTeX();
            texCode += "\n\n";
        }

        texCode += "\\subsubsection{NLP-Ergebnisse}\n\n";

        if (topic != null) {
            texCode += "Topic: " + topic + "\n\n";
        }

        texCode += "Sentiment: " + sentiment + "\n\n";
        texCode += "Positiver Anteil: " + positiv + "\n\n";
        texCode += "Negativer Anteil: " + negativ + "\n\n";
        texCode += "Neutraler Anteil: " + neutral + "\n\n";

        return texCode;
    }
}
