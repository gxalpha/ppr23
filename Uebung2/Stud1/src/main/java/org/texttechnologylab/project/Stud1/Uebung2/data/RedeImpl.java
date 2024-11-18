package org.texttechnologylab.project.Stud1.Uebung2.data;

import org.texttechnologylab.project.data.Abgeordneter;
import org.texttechnologylab.project.data.Rede;

import java.sql.Date;
import java.util.List;

/**
 * Private Implementierung von Rede
 */
class RedeImpl extends BundestagObjectImpl implements Rede {
    private final String id;
    private final Abgeordneter redner;
    private final List<String> text;
    private final Date date;

    /**
     * @param id     ID der Rede
     * @param redner Redner
     * @param text   Text der Rede, als Liste von Abschnitten
     * @param date   Datum der Rede
     */
    RedeImpl(String id, Abgeordneter redner, List<String> text, Date date) {
        this.id = id;
        this.redner = redner;
        /* Der Text bekommt später ggf. noch Kommentare etc, daher als Ausschnitte */
        this.text = text;
        this.date = date;
    }

    /**
     * @return Redner der Rede
     */
    @Override
    public Abgeordneter getRedner() {
        return redner;
    }

    /**
     * @return Text der Rede
     */
    @Override
    public String getText() {
        return String.join("\n", text);
    }

    /**
     * @return Datum der Rede
     */
    @Override
    public Date getDate() {
        return date;
    }

    /**
     * @return Kurzinformation der Rede
     */
    @Override
    public String getLabel() {
        return "Rede von " + redner.getLabel() + " am " + date;
    }

    /**
     * @return ID der Rede
     */
    @Override
    public Object getID() {
        return id;
    }

}
