package org.texttechnologylab.project.Stud2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.data.BundestagObject;
import org.texttechnologylab.project.Stud2.exception.BundestagException;

/**
 * Oberklasse für alle Objekte in der Anwendung *Inside Bundestag*
 *
 * @author Stud2
 */
public class BundestagObject_Impl implements BundestagObject {

    private final String label;

    /**
     * Konstruktor für ein Bundestagsobjekt
     *
     * @param label Der eindeutige Bezeichner des Bundestagsobjekts
     */
    public BundestagObject_Impl(String label) {
        this.label = label;
    }

    /**
     * @return Gibt den per Default hashCode (ID) des Objektes zurück
     */
    @Override
    public Object getID() {
        return this.hashCode();
    }

    /**
     * @return Gibt das Label des Objektes zurück
     */
    @Override
    public String getLabel() {
        return this.label;
    }

    /**
     * @param o Das zu vergleichende BundestagObjekt
     * @return Gibt zurück, ob die Labels lexikografisch gleich sind
     */
    @Override
    public int compareTo(BundestagObject o) {
        return this.label.compareTo(o.getLabel());
    }

    /**
     * @return das Object als Document
     */
    @Override
    public Document toDoc() throws BundestagException {
        return new Document();
    }
}
