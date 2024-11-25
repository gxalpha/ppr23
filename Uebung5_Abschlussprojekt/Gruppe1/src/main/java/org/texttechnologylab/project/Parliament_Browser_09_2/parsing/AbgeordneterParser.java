package org.texttechnologylab.project.Parliament_Browser_09_2.parsing;

import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.Set;

/**
 * Interface zum Parsen der Abgeordneten
 *
 * @author Stud
 */
public interface AbgeordneterParser {

    /**
     * Methode, die die Stammdaten der Abgeordneten aus BundestagsXML parst
     *
     * @param stammdatenXML XML, die alle Stammdaten der Abgeordneten enthält
     * @author Stud
     */
    void parseStammdaten(InputStream stammdatenXML) throws ParserConfigurationException, IOException, SAXException;

    /**
     * Methode, die Liste der geparsten Abgeordneten zurückgibt
     *
     * @return Liste der geparsten Abgeordneten
     * @author Stud
     */
    Set<Abgeordneter> getAbgeordnete();

    /**
     * Hilfsfunktion, die aus String ein Datum erstellt
     *
     * @param dateString Datumsstring der Datum als TT.MM.JJJJ enthält
     * @return sql Datum des Abgeordneten
     * @author Stud
     */
    Date parseDateFromString(String dateString);

    /**
     * Hilfsfunktion, um Inhalte von XML-Elementen zu erhalten
     *
     * @param element Element der XML Datei
     * @param tagName Tag, nach welchem im Element gesucht werden soll
     * @return String als Inhalt des Elements
     * @author Stud
     */
    String getContentWithTagName(Element element, String tagName);
}
