package org.texttechnologylab.project.Parliament_Browser_09_2.parsing;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Class for parsing xml Files.
 *
 * @author Stud based on code from uebung 1-4 by Stud.
 */
public class XMLParser {

    List<InputStream> streamList;


    /**
     * Constructor for the class XMLParser.
     *
     * @param streamList list of inputstreams
     * @author Stud
     */
    public XMLParser(List<InputStream> streamList) {
        this.streamList = streamList;
    }

    /**
     * Parses the XML file and returns a Document representing the XML file.
     *
     * @param protocol xml file as string
     * @return document object representing the xml file.
     * @throws ParserConfigurationException
     * @author Stud
     */
    public static org.w3c.dom.Document getDocument(String protocol) throws ParserConfigurationException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); // TODO
        DocumentBuilder builder;

        {
            try {
                builder = factory.newDocumentBuilder();

                protocol = protocol.replace("\uFEFF", ""); // It's a shame that this is needed
                // Get Document
                InputSource source = new InputSource(new StringReader(protocol));
                org.w3c.dom.Document document = builder.parse(source);

                // Normalize the xML structure
                document.getDocumentElement().normalize();

                return document;

            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Method to create list of documents representing the xml files.
     *
     * @return list of w3c.dom Documents
     * @throws ParserConfigurationException
     * @author Stud
     */
    public List<Document> create_documentList() throws ParserConfigurationException, IOException, SAXException {

        List<Document> documentList = new ArrayList<>();

        for (InputStream inputStream : this.streamList) {
            Document doc = getDocument(new String(inputStream.readAllBytes()));
            documentList.add(doc);
        }

        return documentList;

    }


}
