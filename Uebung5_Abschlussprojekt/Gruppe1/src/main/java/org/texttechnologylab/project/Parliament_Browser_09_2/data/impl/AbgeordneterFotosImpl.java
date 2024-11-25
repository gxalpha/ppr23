package org.texttechnologylab.project.Parliament_Browser_09_2.data.impl;

import org.bson.Document;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Abgeordneter;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.AbgeordneterFotos;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;

import java.io.IOException;


/**
 * Implementation of the AgeordnetenFotos Interface for the database
 *
 * @author Stud
 */
public class AbgeordneterFotosImpl implements AbgeordneterFotos {

    private final String id;
    private final String label;
    private final String alt;
    private final String photographerName;
    private final String url;
    private boolean primary;

    private String abgeordnetenID;

    /**
     * Konstruktor für die Klasse AbgeordneterFotosImpl.
     *
     * @param id ID des Fotos
     * @param label Beschreibung des Fotos
     * @param alt Informationen über die Person/Personen auf dem Foto.
     * @param photographerName Name des Fotografen
     * @param url Link zu dem Foto
     * @param primary bestimmt, ob das Bild ein Primärbild ist.
     * @throws IOException
     * @author Stud
     */
    public AbgeordneterFotosImpl(String id, String label, String alt, String photographerName, String url, boolean primary) throws IOException {
        this.id = id;
        this.label = label;
        this.alt = alt;
        this.photographerName = photographerName;
        this.url = url;
        this.primary = primary;
    }


    /**
     * Konstruktor für AbgeordneterFotosImpl in ihrer DB-Darstellung.
     *
     * @param document Document aus der DB.
     * @author Stud
     */
    public AbgeordneterFotosImpl(Document document) {
        this.id = document.getString("_id");
        this.label = document.getString("label");
        this.alt = document.getString("alt");
        this.abgeordnetenID = document.getString("abgeordnetenID");
        this.photographerName = document.getString("photographer");
        this.url = document.getString("url");
        this.primary = (boolean) document.get("primary");
    }


    /**
     * @return id des Fotos.
     */
    @Override
    public String getID() {
        return this.id;
    }


    /**
     * Get a document to represent a picture in the database
     *
     * @return the document
     */
    @Override
    public Document toDoc() {
        Document document = new Document();
        document.append("_id", this.id);
        document.append("label", this.label);
        document.append("alt", this.alt);
        document.append("abgeordnetenID", this.abgeordnetenID);
        document.append("photographer", this.photographerName);
        document.append("url", this.url);
        document.append("primary", this.primary);
        return document;
    }


    /**
     * @return Name des Fotografen
     */
    @Override
    public String getPhotographer() {
        return this.photographerName;
    }


    /**
     * @return Beschreibung des Bildes
     */
    @Override
    public String getLabel() {
        return this.label;
    }


    /**
     * @return Link zu dem Bild
     */
    @Override
    public String getURL() {
        return this.url;
    }


    /**
     * @return Informationen zu dem Bild
     */
    @Override
    public String getAlt() {
        return this.alt;
    }


    /**
     * @return boolean, ob das Bild ein Primärbild ist
     */
    @Override
    public boolean isPrimary() {
        return primary;
    }


    /**
     * Primärbild Festlegung
     * @param primary boolean
     */
    @Override
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    /**
     * @return Abgeordneten ID der Person auf dem Bild
     */
    @Override
    public String getAbgeordnetenID() {
        return abgeordnetenID;
    }

    /**
     * Fügt dem Bild die ID des Abgeordneten hinzu, der auf dem Bild abgebildet ist.
     *
     * @param vorname Name des Abgeordneten
     * @param nachname Nachname des Abgeordneten
     * @param mongoDBHandler
     * @throws IOException
     * @author Stud
     */
    @Override
    public void setAbgeordnetenID(String vorname, String nachname, MongoDBHandler mongoDBHandler) throws IOException {

        Abgeordneter abgeordneter = mongoDBHandler.getAbgeordneterByName(vorname, nachname);
        assert abgeordneter != null;

        String abgeordnetenID = abgeordneter.getID();

        this.abgeordnetenID = abgeordnetenID;
    }

}
