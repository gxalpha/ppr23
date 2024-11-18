package org.texttechnologylab.project.Stud2.personen;

import org.texttechnologylab.project.Stud2.impl.Fraktion;
import org.texttechnologylab.project.Stud2.impl.Mitarbeiter;
import org.texttechnologylab.project.Stud2.impl.Raumbelegung;

import java.util.Date;

// Represents a person which sits in the parliament:
public interface AbgeordneterIF {
    public int AbgeordnetenID = 0;
    public int budget = 0;
    public Mitarbeiter[] angestellte = null;
    public Raumbelegung[ ] termine = null;
    public Fraktion fraktion = null;
    public String partei_kurz = null;
    public String vita_kurz = null;
    public String veroeffentlichungspflichtiges = null;
    public int wahlperiode = 0;
    public Date mdbwp_von = null;
    public Date mdbwp_bis = null;
    public int wkr_nummer = 0;
    public String wkr_name = null;
    public String wkr_land = null;
    public String liste = null;
    public String mandatsart = null;
    public String[] institutionen = null;

    public int getAbgeordnetenID();

    public void setAbgeordnetenID(int abgeordnetenID);

    public int getBudget();

    public void setBudget(int budget);

    public Mitarbeiter[] getAngestellte();

    public void setAngestellte(Mitarbeiter[] angestellte);

    public Raumbelegung[] getTermine();

    public void setTermine(Raumbelegung[] termine);

    public Fraktion getFraktion();

    public void setFraktion(Fraktion fraktion);

    public String getPartei_kurz();

    public void setPartei_kurz(String partei_kurz);

    public String getVita_kurz();

    public void setVita_kurz(String vita_kurz);

    public String getVeroeffentlichungspflichtiges();

    public void setVeroeffentlichungspflichtiges(String veroeffentlichungspflichtiges);

    public int getWahlperiode();

    public void setWahlperiode(int wahlperiode);

    public Date getMdbwp_von();

    public void setMdbwp_von(Date mdbwp_von);

    public Date getMdbwp_bis();

    public void setMdbwp_bis(Date mdbwp_bis);

    public int getWkr_nummer();

    public void setWkr_nummer(int wkr_nummer);

    public String getWkr_name();

    public void setWkr_name(String wkr_name);

    public String getWkr_land();

    public void setWkr_land(String wkr_land);

    public String getListe();

    public void setListe(String liste);

    public String getMandatsart();

    public void setMandatsart(String mandatsart);

    public String[] getInstitutionen();

    public void setInstitutionen(String[] institutionen);

}
