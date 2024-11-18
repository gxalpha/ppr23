package org.texttechnologylab.project.Stud1.data;

import java.util.Set;

/**
 * Interface für die Tagesordnung.
 * Warum auch immer das unterschiedlich zur Sitzung ist.
 * Reserved for future use, oder so.
 */
public interface Tagesordnung {
    /**
     * @return ID der Tagesordnung (und Sitzung)
     */
    String getID();

    /**
     * @return Sitzung, dessen Tagesordnung das ist
     */
    Sitzung getSitzung();

    /**
     * @return Tagesordnungspunkte auf der Tagesordnung
     */
    Set<String> getTagesordnungspunkte();
}
