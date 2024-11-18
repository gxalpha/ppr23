package org.texttechnologylab.project.Stud2.impl.data;

import org.texttechnologylab.project.data.Ausschuss;

/**
 * Klasse für einen Ausschuss
 *
 * @author Stud2
 */
public class AusschussImpl extends GruppeImpl implements Ausschuss {
    private final String typ;

    /**
     * Konstruktor für die Klasse Ausschuss
     *
     * @param label der eindeutige Bezeichner des Ausschusses
     * @param typ der Typ des Ausschusses (Ausschuss/Unterausschuss/Untersuchungsausschuss)
     */
    public AusschussImpl(String label, String typ) {
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
