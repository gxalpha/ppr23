package org.texttechnologylab.project.Stud2.besuch;

import org.texttechnologylab.project.Stud2.impl.Abgeordneter;
import org.texttechnologylab.project.Stud2.impl.Gast;
import org.texttechnologylab.project.Stud2.impl.Raum;

import java.sql.Time;
import java.util.Date;

// An object of this class represents a specific visit
public interface BesuchIF {
    public int besuchsID = 0;
    public Date besuchsdatum = null;
    public Time beginn = null;
    public Time ende = null;
    public Gast[] gaeste = null;
    public Raum raum = null;
    public Abgeordneter[] anwesende_abgeordnete = null;

    public int getBesuchsID();

    public Date getBesuchsdatum();

    public void setBesuchsdatum(Date besuchsdatum);

    public Time getBeginn();

    public void setBeginn(Time beginn);

    public Time getEnde();

    public void setEnde(Time ende);

    public Gast[] getGaeste();

    public void setGaeste(Gast[] gaeste);

    public Raum getRaum();

    public void setRaum(Raum raum);

    public Abgeordneter[] getAnwesende_abgeordnete();

    public void setAnwesende_abgeordnete(Abgeordneter[] anwesende_abgeordnete);
}
