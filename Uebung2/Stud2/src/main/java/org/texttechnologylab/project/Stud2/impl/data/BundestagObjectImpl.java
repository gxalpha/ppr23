package org.texttechnologylab.project.Stud2.impl.data;

import org.texttechnologylab.project.data.BundestagObject;

/**
 * Oberklasse für alle Objekte in der Anwendung *Inside Bundestag*
 *
 * @author Stud2
 */
public class BundestagObjectImpl implements BundestagObject {

    private final String label;

    /**
     * Konstruktor für ein Bundestagsobjekt
     *
     * @param label Der eindeutige Bezeichner des Bundestagsobjekts
     */
    public BundestagObjectImpl(String label) {
        this.label = label;
    }

    /**
     * @return Gibt den hashCode (ID) des Objektes zurück
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
}
