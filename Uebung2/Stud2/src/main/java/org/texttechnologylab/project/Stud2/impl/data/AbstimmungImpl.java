package org.texttechnologylab.project.Stud2.impl.data;

import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Abstimmung;
import org.texttechnologylab.project.data.Types;
import org.texttechnologylab.project.exception.BundestagException;
import org.texttechnologylab.project.Stud2.impl.exception.AttributeNotFoundError;

/**
 * Klasse für eine Abstimmung
 *
 * @author Stud2
 */
public class AbstimmungImpl extends BundestagObjectImpl implements Abstimmung {
    private final Abgeordneter abgeordneter;
    private final Types.ABSTIMMUNG ergebnis;
    private final String beschreibung;

    /**
     * Konstruktor für ein Objekt der Klasse Abstimmung
     *
     * @param label der Bezeichner der Abstimmung der Form *Datum: Thema der Abstimmung*
     * @param abgeordneter der Abgeordnete, der abstimmt
     * @param ergebnis die Stimme des Abgeordneten
     * @param beschreibung das Thema der Abstimmung
     */
    public AbstimmungImpl(String label, Abgeordneter abgeordneter, Types.ABSTIMMUNG ergebnis,
                          String beschreibung) {
        super(label);
        this.abgeordneter = abgeordneter;
        this.ergebnis = ergebnis;
        this.beschreibung = beschreibung;
    }

    /**
     * @return Gibt den Abgeordneten zurück, der abgestimmt hat
     */
    @Override
    public Abgeordneter getAbgeordneter() {
        return this.abgeordneter;
    }

    /**
     * @return Gibt das Abstimmungsergebnis zurück
     */
    @Override
    public Types.ABSTIMMUNG getErgebnis() {
        return this.ergebnis;
    }

    /**
     * @return Gibt die Beschreibung der Abstimmung zurück
     * @throws BundestagException falls keine Beschreibung vorhanden
     */
    @Override
    public String getBeschreibung() throws BundestagException {
        if (beschreibung == null || beschreibung.equals(" ")) {
            throw new AttributeNotFoundError("Die Beschreibung der Abstimmung ist null oder leer.");
        }
        return this.beschreibung;
    }
}
