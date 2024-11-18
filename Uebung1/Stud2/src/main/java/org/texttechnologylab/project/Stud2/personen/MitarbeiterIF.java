package org.texttechnologylab.project.Stud2.personen;

import org.texttechnologylab.project.Stud2.impl.Abgeordneter;
import org.texttechnologylab.project.Stud2.impl.Raumbelegung;

// objects of this class work for objects of the class 'Abgeordneter'
public interface MitarbeiterIF {
    public int mitarbeiterID = 0;
    public Abgeordneter vorgesetzter = null;
    public int beschaeftigungsumfang = 0;
    public Raumbelegung[] termine = null;

    // Getter and Setter
    public int getMitarbeiterID();

    public void setMitarbeiterID(int mitarbeiterID);
    public Abgeordneter getVorgesetzter();

    public void setVorgesetzter(Abgeordneter vorgesetzter);

    public int getBeschaeftigungsumfang();

    public void setBeschaeftigungsumfang(int beschaeftigungsumfang);

    public Raumbelegung[] getTermine();

    public void setTermine(Raumbelegung[] termine);
}
