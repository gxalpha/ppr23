package org.texttechnologylab.project.Stud2.data;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.exception.BundestagException;

/**
 * Ein Interface für ein Bundestagsobjekt
 *
 * @author Stud2
 */
public interface BundestagObject extends Comparable<BundestagObject> {

    /**
     * @return Gibt per Default den hashCode (ID) des Objektes zurück
     */
    Object getID();

    /**
     * @return Gibt das Label des Objektes zurück
     */
    String getLabel();

    /**
     * @param o Das zu vergleichende BundestagObjekt
     * @return Gibt zurück, ob die Labels lexikografisch gleich sind
     */
    int compareTo(BundestagObject o);

    /**
     * @return das Object als Document
     */
    Document toDoc() throws BundestagException;
}
