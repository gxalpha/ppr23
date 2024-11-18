package org.texttechnologylab.project.Stud2.personen;

import org.texttechnologylab.project.Stud2.impl.Abgeordneter;

import java.util.Date;

// Objects of this class represent a worker of the Bundestagsverwaltung
public interface PersonalBundestagsverwaltungIF {
    public int personalID = 0;
    public Date vertrag_von = null;
    public Date vertrag_bis = null;

    // This person is able to send a Raumbelegung to an Abgeordneter and his Mitarbeiter
    public void raumplan_senden(Abgeordneter a);

    public int getPersonalID();

    public void setPersonalID(int personalID);

    public Date getVertrag_von();

    public void setVertrag_von(Date vertrag_von);

    public Date getVertrag_bis();

    public void setVertrag_bis(Date vertrag_bis);
}
