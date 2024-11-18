package org.texttechnologylab.project.Stud2.impl;

import org.texttechnologylab.project.Stud2.gruppierungen.FraktionIF;

// A Fraction is a group of at least 3 Abgeordneten:
public class Fraktion implements FraktionIF {
    private int fraktionsID;
    private Abgeordneter[] abgeordnete;
    private String name;

    // Getter and Setter
    public int getFraktionsID() {
        return fraktionsID;
    }

    public Abgeordneter[] getAbgeordnete() {
        return abgeordnete;
    }

    public void setAbgeordnete(Abgeordneter[] abgeordnete) {
        this.abgeordnete = abgeordnete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
