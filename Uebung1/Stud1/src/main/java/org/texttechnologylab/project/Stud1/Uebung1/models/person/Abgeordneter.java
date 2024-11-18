package org.texttechnologylab.project.Stud1.Uebung1.models.person;

import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.BiografischeAngaben;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.Institution;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.Name;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.Wahlperiode;

/**
 * Ein Abgeordneter.<br>
 * Enthält alle Daten der Person, sowie die Institutionen (in denen der Abgeordnete ist) und die Wahlperioden, für die er gewählt wurde.
 */
public interface Abgeordneter extends Person {
    public Institution[] getInstitutionen();

    public void setInstitutionen(Institution[] institutionen);

    public Wahlperiode[] getWahlperioden();

    public void setWahlperioden(Wahlperiode[] wahlperioden);
}

class AbgeordneterImpl implements Abgeordneter {
    private Institution[] institutionen;
    private Wahlperiode[] wahlperioden;
    private Name[] namen;
    private BiografischeAngaben biografischeAngaben;
    private String email;

    public AbgeordneterImpl(Institution[] institutionen, Wahlperiode[] wahlperioden, Name[] namen, BiografischeAngaben biografischeAngaben, String email) {
        this.institutionen = institutionen;
        this.wahlperioden = wahlperioden;
        this.namen = namen;
        this.biografischeAngaben = biografischeAngaben;
        this.email = email;
    }

    @Override
    public Institution[] getInstitutionen() {
        return institutionen;
    }

    @Override
    public void setInstitutionen(Institution[] institutionen) {
        this.institutionen = institutionen;
    }

    @Override
    public Wahlperiode[] getWahlperioden() {
        return wahlperioden;
    }

    @Override
    public void setWahlperioden(Wahlperiode[] wahlperioden) {
        this.wahlperioden = wahlperioden;
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
