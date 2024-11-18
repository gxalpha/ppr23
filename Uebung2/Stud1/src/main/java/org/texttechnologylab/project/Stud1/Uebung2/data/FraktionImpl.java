package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Fraktion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Private Implementierung von Fraktion
 */
class FraktionImpl extends GruppeImpl implements Fraktion {
    // For future use
    private final Map<String, Set<Abgeordneter>> funktionaere;

    /**
     * @param label Name der Fraktion
     */
    FraktionImpl(String label) {
        super(label);
        this.funktionaere = new HashMap<>();
    }

    /**
     * @param abgeordneter Der Abgeordnete
     */
    @Override
    @Deprecated
    void addMember(Abgeordneter abgeordneter) {
        throw new RuntimeException("Nutze für Fraktionen addMember(Abgeordneter, String)");
    }

    /**
     * Fügt einen Abgeordneten einer Fraktion hinzu.
     *
     * @param abgeordneter Der Abgeordnete
     * @param funktion     Funktion des Abgeordneten in der Fraktion. Darf null sein.
     */
    void addMember(Abgeordneter abgeordneter, String funktion) {
        super.addMember(abgeordneter);
        if (funktion != null && !funktion.isEmpty()) {
            Set<Abgeordneter> abgeordnete;
            if (funktionaere.containsKey(funktion)) {
                abgeordnete = funktionaere.get(funktion);
            } else {
                abgeordnete = new HashSet<>();
            }
            abgeordnete.add(abgeordneter);
        }
    }

    /**
     * @return Funktionäre der Fraktion
     */
    @Override
    public Map<String, Abgeordneter> getFunktionaere() {
        /* Diese Funktion ergibt keinen Sinn. Das Problem ist, dass
         * es mehrere Funktionäre mit gleicher Funktion geben kann,
         * die Map also auf eine Menge von Abgeordneten abbilden
         * muss, nicht auf einen einzelnen Abgeordneten. */
        throw new RuntimeException("Diese Funktion ist nicht verfügbar. Siehe Kommentar.");
    }
}
