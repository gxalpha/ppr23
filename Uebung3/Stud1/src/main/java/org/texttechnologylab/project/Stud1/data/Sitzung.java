package org.texttechnologylab.project.Stud1.data;

import java.util.Date;
import java.util.Set;

/**
 * Interface für eine Sitzung.
 */
public interface Sitzung {
    /**
     * @return ID der Sitzung (und Tagesordnung). Unique.
     */
    String getID();

    /**
     * @return Datum der Sitzung
     */
    Date getDate();

    /**
     * @return Dauer der Sitzung, in Minuten
     */
    int getDauer();

    /**
     * @return Tagesordnung der Sitzung
     */
    Tagesordnung getTagesordnung();

    /**
     * @return Reden, die während einem der TOP gehalten wurden
     */
    Set<Rede> getReden();
}
