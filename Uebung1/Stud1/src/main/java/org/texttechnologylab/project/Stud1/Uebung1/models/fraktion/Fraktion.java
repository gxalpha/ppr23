package org.texttechnologylab.project.Stud1.Uebung1.models.fraktion;

import org.texttechnologylab.project.Stud1.Uebung1.models.person.Abgeordneter;

/**
 * Eine Fraktion.<br>
 * Enthält Name der Fraktion und die Abgeordnete, die in ihr sind.
 */
public interface Fraktion {
    public String getName();

    public void setName(String name);

    public Abgeordneter[] getAbgeordnete();

    public void setAbgeordnete(Abgeordneter[] abgeordnete);
}

class FraktionImpl implements Fraktion {
    private String name;
    private Abgeordneter[] abgeordnete;

    public FraktionImpl(String name, Abgeordneter[] abgeordnete) {
        this.name = name;
        this.abgeordnete = abgeordnete;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Abgeordneter[] getAbgeordnete() {
        return abgeordnete;
    }

    @Override
    public void setAbgeordnete(Abgeordneter[] abgeordnete) {
        this.abgeordnete = abgeordnete;
    }
}
