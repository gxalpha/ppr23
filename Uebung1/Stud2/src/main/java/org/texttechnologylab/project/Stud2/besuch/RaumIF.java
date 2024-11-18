package org.texttechnologylab.project.Stud2.besuch;

import org.texttechnologylab.project.Stud2.impl.Raumbelegung;

// Objects of this class represent a room where visits can take place:
public interface RaumIF {
    public int raumID = 0;
    public int kapazitaet = 0;

    public Raumbelegung raumbelegung = null;

    // Getter and Setter
    public int getRaumID();

    public int getKapazitaet();

    public void setKapazitaet(int kapazitaet);

    public Raumbelegung getRaumbelegung();

    public void setRaumbelegung(Raumbelegung raumbelegung);
}
