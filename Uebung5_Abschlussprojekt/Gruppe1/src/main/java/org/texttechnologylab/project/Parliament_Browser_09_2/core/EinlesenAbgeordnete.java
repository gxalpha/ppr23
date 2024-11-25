package org.texttechnologylab.project.Parliament_Browser_09_2.core;

import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.AbgeordneterDBImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.MongoDBHandlerImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.parsing.AbgeordneterParser;
import org.texttechnologylab.project.Parliament_Browser_09_2.parsing.impl.AbgeordneterParserImpl;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Klasse zum Einlesen der Abgeordneten
 *
 * @author Stud
 */
public class EinlesenAbgeordnete {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        AbgeordneterParser abgeordneterParser = new AbgeordneterParserImpl();

        // XML mit Abgeordneten holen
        InputStream stammdaten = EinlesenAbgeordnete.class.getClassLoader().getResourceAsStream("MdB-Stammdaten/MDB_STAMMDATEN.XML");

        // Abgeordnete parsen
        abgeordneterParser.parseStammdaten(stammdaten);

        // geparste Abgeordnete holen
        Set<Abgeordneter> abgeordneterSet = abgeordneterParser.getAbgeordnete();

        // Abgeordnete in Datenbank schreiben
        AbgeordneterDBImpl abgeordneterInDb = new AbgeordneterDBImpl(new MongoDBHandlerImpl("Project_09_02.txt"));

        abgeordneterInDb.insertAbgeordnetenDB(abgeordneterSet);
    }
}
