package org.texttechnologylab.project.Stud1.database;

import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.texttechnologylab.project.Stud1.data.Abgeordneter;
import org.texttechnologylab.project.Stud1.data.Rede;
import org.texttechnologylab.project.Stud1.data.RedeNLP;
import org.texttechnologylab.project.Stud1.data.SentenceNLP;
import org.texttechnologylab.project.Stud1.data.impl.Abgeordneter_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.RedeNLP_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.Rede_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.data.impl.SentenceNLP_MongoDB_Impl;
import org.texttechnologylab.project.Stud1.website.LogEntry;
import org.texttechnologylab.project.Stud1.website.LogEntry_MongoDB_Impl;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.util.stream.Collectors;

/**
 * Hilfsklasse zum Erstellen von Dokumenten
 */
class MongoDBConversionHelper {

    /**
     * Bildet einen Abgeordneten auf ein Document ab
     *
     * @param abgeordneter Der Abgeordnete
     * @return Das Dokument
     */
    public static Document toDocument(Abgeordneter abgeordneter) {
        Document document = new Document();
        document.append(Abgeordneter_MongoDB_Impl.Keys.ID, abgeordneter.getID());
        document.append(Abgeordneter_MongoDB_Impl.Keys.NACHNAME, abgeordneter.getNachname());
        document.append(Abgeordneter_MongoDB_Impl.Keys.VORNAME, abgeordneter.getVorname());
        document.append(Abgeordneter_MongoDB_Impl.Keys.ORTSZUSATZ, abgeordneter.getOrtszusatz());
        document.append(Abgeordneter_MongoDB_Impl.Keys.NAMENSPRAEFIX, abgeordneter.getNamenspraefix());
        document.append(Abgeordneter_MongoDB_Impl.Keys.ADELSSUFFIX, abgeordneter.getAdelssuffix());
        document.append(Abgeordneter_MongoDB_Impl.Keys.ANREDE, abgeordneter.getAnrede());
        document.append(Abgeordneter_MongoDB_Impl.Keys.AKADEMISCHER_TITEL, abgeordneter.getAkademischerTitel());
        document.append(Abgeordneter_MongoDB_Impl.Keys.GEBURTSDATUM, abgeordneter.getGeburtsdatum());
        document.append(Abgeordneter_MongoDB_Impl.Keys.GEBURTSORT, abgeordneter.getGeburtsort());
        document.append(Abgeordneter_MongoDB_Impl.Keys.STERBEDATUM, abgeordneter.getSterbedatum());
        document.append(Abgeordneter_MongoDB_Impl.Keys.GESCHLECHT, abgeordneter.getGeschlecht());
        document.append(Abgeordneter_MongoDB_Impl.Keys.RELIGION, abgeordneter.getReligion());
        document.append(Abgeordneter_MongoDB_Impl.Keys.BERUF, abgeordneter.getBeruf());
        document.append(Abgeordneter_MongoDB_Impl.Keys.VITA, abgeordneter.getVita());
        document.append(Abgeordneter_MongoDB_Impl.Keys.PARTEI, abgeordneter.getPartei());
        document.append(Abgeordneter_MongoDB_Impl.Keys.FRAKTION, abgeordneter.getFraktion());
        document.append(Abgeordneter_MongoDB_Impl.Keys.REDEN, abgeordneter.getReden().stream().map(Rede::getID).collect(Collectors.toList()));
        document.append(Abgeordneter_MongoDB_Impl.Keys.MANDATE, abgeordneter.getMandate());
        document.append(Abgeordneter_MongoDB_Impl.Keys.MITGLIEDSCHAFTEN, abgeordneter.getMitgliedschaften());
        return document;
    }

    /**
     * Bildet eine Rede auf ein Document ab
     *
     * @param rede Die Rede
     * @return Das Dokument
     */
    public static Document toDocument(Rede rede) {
        Document document = new Document();
        document.append(Rede_MongoDB_Impl.Keys.ID, rede.getID());
        document.append(Rede_MongoDB_Impl.Keys.REDNER, rede.getRedner().getID());
        document.append(Rede_MongoDB_Impl.Keys.TEXT, rede.getText());
        document.append(Rede_MongoDB_Impl.Keys.TEXT_LENGTH, rede.getText().split(" ").length);
        document.append(Rede_MongoDB_Impl.Keys.KOMMENTARE, rede.getKommentare());
        document.append(Rede_MongoDB_Impl.Keys.DATUM, rede.getDate());
        return document;
    }

    /**
     * Serialisiert ein JCas zu einem MongoDB-Dokument
     *
     * @param jCas Das JCas
     * @return Das Dokument
     */
    public static Document toDocument(JCas jCas, String redeID) throws SAXException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XmiCasSerializer.serialize(jCas.getCas(), outputStream);
        String content = outputStream.toString();

        Document document = new Document();
        document.put(Rede_MongoDB_Impl.JCasKeys.ID, redeID);
        document.put(Rede_MongoDB_Impl.JCasKeys.CONTENT, content);
        return document;
    }

    /**
     * Serialisiert ein RedeNLP-Objekt zu einem MongoDB-Dokument.
     * Ohne ID da nicht Top-Level
     *
     * @param nlpDaten Die RedeNLP Daten
     * @return Das Dokument
     */
    public static Document toDocument(RedeNLP nlpDaten) {
        Document document = new Document();
        document.append(RedeNLP_MongoDB_Impl.Keys.SENTIMENT, nlpDaten.getSentiment());
        document.append(RedeNLP_MongoDB_Impl.Keys.NOUNS, nlpDaten.getNouns());
        document.append(RedeNLP_MongoDB_Impl.Keys.NAMED_ENTITIES, nlpDaten.getNamedEntities());
        document.append(RedeNLP_MongoDB_Impl.Keys.SENTENCES, nlpDaten.getSentences().stream().map(MongoDBConversionHelper::toDocument).collect(Collectors.toList()));
        return document;
    }

    /**
     * Serialisiert ein SentenceNLP-Objekt zu einem MongoDB-Dokument.
     * Ohne ID da nicht Top-Level
     *
     * @param sentence Die SentenceNLP Daten
     * @return Das Dokument
     */
    public static Document toDocument(SentenceNLP sentence) {
        Document document = new Document();
        document.append(SentenceNLP_MongoDB_Impl.Keys.SENTIMENT, sentence.getSentiment());
        document.append(SentenceNLP_MongoDB_Impl.Keys.TEXT, sentence.getText());
        return document;
    }

    /**
     * Serialisiert einen Log-Eintrag
     *
     * @param logEntry Der Log-Eintrag
     * @return Das serialisierte MongoDB-Dokument
     */
    public static Document toDocument(LogEntry logEntry) {
        Document document = new Document();
        document.append(LogEntry_MongoDB_Impl.Keys.TIMESTAMP, logEntry.getTimestamp().getEpochSecond());
        document.append(LogEntry_MongoDB_Impl.Keys.ROUTE, logEntry.getRoute());
        Document params = new Document();
        logEntry.getParams().forEach(params::append);
        document.append(LogEntry_MongoDB_Impl.Keys.PARAMS, params);
        return document;
    }
}
