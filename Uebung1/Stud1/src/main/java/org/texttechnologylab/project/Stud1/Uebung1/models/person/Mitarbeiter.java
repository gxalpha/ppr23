package org.texttechnologylab.project.Stud1.Uebung1.models.person;

import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.BiografischeAngaben;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.Name;

/**
 * Ein Mitarbeiter.<br>
 * Enthält alle Daten der Person, sowie den übergeordneten Abgeordneten und den Beschäftigungsumfang.
 */
public interface Mitarbeiter extends Person {
    public Abgeordneter getAbgeordneter();

    public void setAbgeordneter(Abgeordneter abgeordneter);

    public int getBeschaeftigungsumfang();

    public void setBeschaeftigungsumfang(int beschaeftigungsumfang);
}

class MitarbeiterImpl implements Mitarbeiter {
    private Abgeordneter abgeordneter;
    private int beschaeftigungsumfang;
    private Name[] namen;
    private BiografischeAngaben biografischeAngaben;
    private String email;

    public MitarbeiterImpl(Abgeordneter abgeordneter, int beschaeftigungsumfang, Name[] namen, BiografischeAngaben biografischeAngaben, String email) {
        this.abgeordneter = abgeordneter;
        this.beschaeftigungsumfang = beschaeftigungsumfang;
        this.namen = namen;
        this.biografischeAngaben = biografischeAngaben;
        this.email = email;
    }

    @Override
    public Abgeordneter getAbgeordneter() {
        return abgeordneter;
    }

    @Override
    public void setAbgeordneter(Abgeordneter abgeordneter) {
        this.abgeordneter = abgeordneter;
    }

    @Override
    public int getBeschaeftigungsumfang() {
        return beschaeftigungsumfang;
    }

    @Override
    public void setBeschaeftigungsumfang(int beschaeftigungsumfang) {
        this.beschaeftigungsumfang = beschaeftigungsumfang;
    }

    @Override
    public Name[] getNamen() {
        return namen;
    }

    @Override
    public void setNamen(Name[] namen) {
        this.namen = namen;
    }

    @Override
    public BiografischeAngaben getBiografischeAngaben() {
        return biografischeAngaben;
    }

    @Override
    public void setBiografischeAngaben(BiografischeAngaben biografischeAngaben) {
        this.biografischeAngaben = biografischeAngaben;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }
}
