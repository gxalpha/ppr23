package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.BundestagObject;

import java.util.UUID;

/**
 * Private Teil-Implementierung von BundestagObject.
 * Abstrakt, da jede Implementierung getLabel() selbst implementieren soll.
 */
abstract class BundestagObjectImpl implements BundestagObject {
    // Use UUID to prevent any kind of clash
    private final UUID id;

    /**
     * Default-Konstruktor
     */
    BundestagObjectImpl() {
        this.id = UUID.randomUUID();
    }

    /**
     * @return UUID des Objekts
     */
    @Override
    public Object getID() {
        return id;
    }

    /**
     * @param other Anderes Objekt
     * @return 0 wenn die Label der Objekte gleich im Alphabet sind, -1 wenn das Objekt vor bundestagObject im Alphabet liegt, 1 wenn es dahinter liegt
     */
    @Override
    public int compareTo(BundestagObject other) {
        return getLabel().compareTo(other.getLabel());
    }
}
