package org.texttechnologylab.project.Stud2.impl.data;

import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Rede;

import java.sql.Date;

/**
 * Klasse für eine Rede
 *
 * @author Stud2
 */
public class RedeImpl extends BundestagObjectImpl implements Rede {
    private final int ID;
    private final Abgeordneter redner;
    private final String text;
    private final Date date;

    /**
     * Konstruktor für ein Objekt der Klasse Rede
     *
     * @param label der Bezeichner (hier: ID) der Rede
     * @param redner der Abgeordnete, der die Rede hält
     * @param text der Inhalt der Rede
     * @param date das Datum, an dem die Rede gehalten wurde
     */
    public RedeImpl(int ID, String label, Abgeordneter redner, String text, Date date) {
        super(label);
        this.ID = ID;
        this.redner = redner;
        this.text = text;
        this.date = date;
    }

    /**
     * @return die ID der Rede
     */
    @Override
    public Object getID() {
        return this.ID;
    }

    /**
     * @return Gibt den Redner zurück
     */
    @Override
    public Abgeordneter getRedner() {
        return this.redner;
    }

    /**
     * @return Gibt denn Redetext zurück
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * @return Gibt das Datum der Rede zurück
     */
    @Override
    public Date getDate() {
        return this.date;
    }

}
