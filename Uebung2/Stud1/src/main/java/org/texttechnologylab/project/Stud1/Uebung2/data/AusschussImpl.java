package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Ausschuss;

/**
 * Private Implementierung von Ausschuss
 */
class AusschussImpl extends GruppeImpl implements Ausschuss {
    private final String type;

    /**
     * @param label Name des Ausschusses
     * @param type  Typ des Ausschusses
     */
    AusschussImpl(String label, String type) {
        super(label);
        this.type = type;
    }

    /**
     * @return Art des Ausschusses
     */
    @Override
    public String getType() {
        return type;
    }
}
