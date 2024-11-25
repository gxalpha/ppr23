package org.texttechnologylab.project.Parliament_Browser_09_2.data;

import org.bson.Document;

import java.io.IOException;
import java.util.List;

/**
 * Ein Tagesordnungspunkt.
 *
 * @author Stud
 */
public interface Tagesordnungspunkt {
    /**
     * @return Thema des Tagesordnungspunktes
     */
    String getThema();

    /**
     * @return IDs der Reden des Tagesordnungspunktes
     */
    List<String> getRedenIDs();

    /**
     * @return Objekt in ihrer MongoDB-Repräsentation
     */
    Document toDoc();

    /**
     * Tagesordnungspunkt zu Tex-Code
     *
     * @return String mit Tex-Code für den Tagesordnungspunkt
     * @author Stud
     */
    String toTeX() throws IOException;
}
