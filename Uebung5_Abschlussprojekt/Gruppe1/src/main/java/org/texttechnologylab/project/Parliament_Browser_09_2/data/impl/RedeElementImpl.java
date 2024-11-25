package org.texttechnologylab.project.Parliament_Browser_09_2.data.impl;

import org.apache.commons.lang3.EnumUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElement;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElementNLP;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Ein Rede-Element.
 * Ein Rede-Element ist ein Teil der Rede, d.h. ein Absatz, gewöhnlicher Kommentar oder ein Kommentar des Präsidiums.
 *
 * @author Stud
 * @author Stud (Änderungen)
 */
public class RedeElementImpl implements RedeElement {
    private final RedeElement.RedeElementTyp typ;
    private final String text;
    private RedeElementNLP nlp;

    /**
     * Konstruktor
     *
     * @param typ  Typ des Elements (Absatz, Kommentar oder Präsidium)
     * @param text Text des Elements
     * @author Stud
     * @author Stud (Änderungen)
     */
    public RedeElementImpl(RedeElement.RedeElementTyp typ, String text) {
        this.typ = typ;
        /* Entfernt unnötige Zeilenumbrüche und mehrfache Leerzeichen */
        this.text = Arrays.stream(text.replaceAll("\\n", "").split(" "))
                .filter(Predicate.not(String::isEmpty))
                .collect(Collectors.joining(" "));
        this.nlp = null;
    }

    /**
     * Konstruktor zum Erstellen aus der Datenbank
     *
     * @param document MongoDB-Dokument
     * @author Stud
     */
    public RedeElementImpl(Document document) {
        this.typ = EnumUtils.getEnum(RedeElement.RedeElementTyp.class, document.getString("typ"));
        this.text = document.getString("text");
        this.nlp = new RedeElementNLPImpl(document.get("nlp", Document.class));
    }

    /**
     * @return Typ des Rede-Elements
     */
    @Override
    public RedeElementTyp getTyp() {
        return this.typ;
    }

    /**
     * @return Text des Rede-Elements
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * @return NLP-Informationen des RedeElements
     * @author Stud
     */
    @Override
    public RedeElementNLP getNLP() {
        return nlp;
    }

    /**
     * Setzt die NLP-Informationen des RedeElements
     *
     * @param nlp NLP-Informationen
     * @author Stud
     */
    @Override
    public void setNLP(RedeElementNLP nlp) {
        this.nlp = nlp;
    }

    /**
     * @return CAS-Repräsentation der Rede
     */
    @Override
    public JCas toCAS() throws UIMAException {
        return JCasFactory.createText(getText(), "de");
    }

    /**
     * @return Objekt in ihrer MongoDB-Repräsentation
     */
    @Override
    public Document toDoc() {
        Document document = new Document();
        document.put("typ", this.typ.toString());
        document.put("text", this.text);
        document.put("nlp", this.nlp.toDoc());
        return document;
    }

    /**
     * Redeninhalt zu LaTeX
     *
     * @return String, mit dem LaTeX-Code
     * @author Stud
     */
    @Override
    public String toTeX() {
        String texCode = "";

        if (this.typ != RedeElementTyp.ABSATZ) {
            texCode += "\\begin{quote}\n";
        }

        String elementText = this.getText();
        elementText = elementText.replaceAll("\\\\", "\\\\\\\\");
        elementText = elementText.replaceAll("\\{", "\\\\{");
        elementText = elementText.replaceAll("\\}", "\\\\}");
        elementText = elementText.replaceAll("\\}", "\\\\}");
        elementText = elementText.replaceAll("\\$", "\\\\\\$");
        elementText = elementText.replaceAll("#", "\\\\#");
        elementText = elementText.replaceAll("\\^", "\\\\^");
        elementText = elementText.replaceAll("_", "\\\\_");
        elementText = elementText.replaceAll("~", "\\\\~");

        texCode += elementText;

        if (this.typ != RedeElementTyp.ABSATZ) {
            texCode += "\n";
            texCode += "\\end{quote}";
        }

        return texCode;
    }
}
