package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Types;
import org.texttechnologylab.project.data.Wahlperiode;
import org.texttechnologylab.project.exception.BundestagException;

/**
 * Private Implementierung von Abstimmung / ZeitlicheAbstimmung
 */
class AbstimmungImpl extends BundestagObjectImpl implements ZeitlicheAbstimmung {
    private final Wahlperiode wahlperiode;
    private final Abgeordneter abgeordneter;
    private final Types.ABSTIMMUNG ergebnis;
    private final String beschreibung;

    /**
     * @param wahlperiode  Wahlperiode der Abstimmung
     * @param abgeordneter Abgeordneter
     * @param ergebnis     Ergebnis der Abstimmung
     * @param beschreibung Beschreibung der Abstimmung
     */
    AbstimmungImpl(Wahlperiode wahlperiode, Abgeordneter abgeordneter, Types.ABSTIMMUNG ergebnis, String beschreibung) {
        this.wahlperiode = wahlperiode;
        this.abgeordneter = abgeordneter;
        this.ergebnis = ergebnis;
        this.beschreibung = beschreibung;
    }

    /**
     * @return Wahlperiode der Abstimmung
     */
    @Override
    public Wahlperiode getWahlperiode() {
        return wahlperiode;
    }

    /**
     * @return Abgeordneter, der abgestimmt hat
     */
    @Override
    public Abgeordneter getAbgeordneter() {
        return abgeordneter;
    }

    /**
     * @return Ergebnis der Abstimmung
     */
    @Override
    public Types.ABSTIMMUNG getErgebnis() {
        return ergebnis;
    }

    /**
     * @return Beschreibung der Abstimmung
     * @throws BundestagException Wenn es keine Beschreibung hat
     */
    @Override
    public String getBeschreibung() throws BundestagException {
        if (beschreibung == null) {
            throw new BundestagException("Abstimmung hat keine Beschreibung");
        } else {
            return beschreibung;
        }
    }

    /**
     * @return Kurzinformation der Abstimmung
     */
    @Override
    public String getLabel() {
        try {
            return getBeschreibung();
        } catch (BundestagException e) {
            return "Unknown";
        }
    }
}
