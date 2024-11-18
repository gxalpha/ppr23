package org.texttechnologylab.project.Stud2.gruppierungen;

import org.texttechnologylab.project.Stud2.impl.Abgeordneter;
import org.texttechnologylab.project.Stud2.impl.Fraktion;

// A Fraction is a group of at least 3 Abgeordneten:
public interface FraktionIF {
    public int fraktionsID = 0;
    public Abgeordneter[] abgeordnete = null;
    public Fraktion[] fraktionen = null;

    // Getter and Setter
    public int getFraktionsID();

    public Abgeordneter[] getAbgeordnete();

    public void setAbgeordnete(Abgeordneter[] abgeordnete);

    public String getName();

    public void setName(String name);
}
