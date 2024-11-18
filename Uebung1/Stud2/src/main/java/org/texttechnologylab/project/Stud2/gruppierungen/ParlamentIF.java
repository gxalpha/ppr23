package org.texttechnologylab.project.Stud2.gruppierungen;

import org.texttechnologylab.project.Stud2.impl.Abgeordneter;
import org.texttechnologylab.project.Stud2.impl.Fraktion;

// A parliament consists of abgeordnete who are able to get together into fractions:
public interface ParlamentIF {
    public int wahlperiode = 0;
    public Abgeordneter[] abgeordnete = null;
    public Fraktion[] fraktionen = null;

    // Getter and Setter
    public int getWahlperiode();

    public void setWahlperiode(int wahlperiode);

    public Abgeordneter[] getAbgeordnete();

    public void setAbgeordnete(Abgeordneter[] abgeordnete);

    public Fraktion[] getFraktionen();

    public void setFraktionen(Fraktion[] fraktionen);
}
