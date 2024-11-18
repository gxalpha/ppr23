package org.texttechnologylab.project.Stud2.data.impl;

import org.texttechnologylab.project.Stud2.data.Ausschuss;

/**
 * Klasse für einen Ausschuss
 *
 * @author Stud2
 */
public class Ausschuss_Impl extends Gruppe_Impl implements Ausschuss {
    private final String typ;

    /**
     * Konstruktor für die Klasse Ausschuss
     *
     * @param label der eindeutige Bezeichner des Ausschusses
     * @param typ der Typ des Ausschusses (Ausschuss/Unterausschuss/Untersuchungsausschuss)
     */
    public Ausschuss_Impl(String label, String typ) {
        super(label);
        this.typ = typ;
    }

    /**
     * @return Gibt den Typ des Ausschusses zurück
     */
    @Override
    public String getType() {
        return this.typ;
    }

}
