package org.texttechnologylab.project.Stud2.personen;

import org.texttechnologylab.project.Stud2.impl.Abgeordneter;

import java.util.Date;

// Represents a visitor of an Abgeordneter
public interface GastIF {
    public int gastID = 0;
    public Date besuchsdatum = null;
    public Abgeordneter[] eingeladet_von = null;

    // Getter and Setter
    public int getGastID();

    public void setGastID(int gastID);

    public Date getBesuchsdatum();

    public void setBesuchsdatum(Date besuchsdatum);

    public Abgeordneter[] getEingeladet_von();

    public void setEingeladet_von(Abgeordneter[] eingeladet_von);
}
