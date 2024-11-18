package org.texttechnologylab.project.Stud2.besuch;

import org.texttechnologylab.project.Stud2.impl.Besuch;
import org.texttechnologylab.project.Stud2.impl.Raum;

import java.util.Date;

// Represents a plan for a room:
public interface RaumbelegungIF {
    public Raum raum = null;
    public Besuch[] belegungen = null;
    public Date stand = null;

    // Getter and Setter
    public Raum getRaum();

    public void setRaum(Raum raum);

    public Besuch[] getBelegungen();

    public void setBelegungen(Besuch[] belegungen);

    public Date getStand();

    public void setStand(Date stand);
}
