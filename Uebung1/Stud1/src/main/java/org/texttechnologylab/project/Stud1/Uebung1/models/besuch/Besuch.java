package org.texttechnologylab.project.Stud1.Uebung1.models.besuch;

import org.texttechnologylab.project.Stud1.Uebung1.models.person.Abgeordneter;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.Besucher;
import org.texttechnologylab.project.Stud1.Uebung1.models.raum.Raum;

/**
 * Informationen über einen Besuch.<br>
 * Enthält die Besucher, die einladenden Abgeordneten, sowie den Raum in dem der Besuch stattfindet.
 */
public interface Besuch {
    public Besucher[] getBesucher();

    public void setBesucher(Besucher[] besucher);

    public Abgeordneter[] getEinladendeAbgeordnete();

    public void setEinladendeAbgeordnete(Abgeordneter[] einladendeAbgeordnete);

    public Raum getRaum();

    public void setRaum(Raum raum);
}

class BesuchImpl implements Besuch {
    private Besucher[] besucher;
    private Abgeordneter[] einladendeAbgeordnete;
    private Raum raum;

    public BesuchImpl(Besucher[] besucher, Abgeordneter[] einladendeAbgeordnete, Raum raum) {
        this.besucher = besucher;
        this.einladendeAbgeordnete = einladendeAbgeordnete;
        this.raum = raum;
    }

    @Override
    public Besucher[] getBesucher() {
        return besucher;
    }

    @Override
    public void setBesucher(Besucher[] besucher) {
        this.besucher = besucher;
    }

    @Override
    public Abgeordneter[] getEinladendeAbgeordnete() {
        return einladendeAbgeordnete;
    }

    @Override
    public void setEinladendeAbgeordnete(Abgeordneter[] einladendeAbgeordnete) {
        this.einladendeAbgeordnete = einladendeAbgeordnete;
    }

    @Override
    public Raum getRaum() {
        return raum;
    }

    @Override
    public void setRaum(Raum raum) {
        this.raum = raum;
    }
}
