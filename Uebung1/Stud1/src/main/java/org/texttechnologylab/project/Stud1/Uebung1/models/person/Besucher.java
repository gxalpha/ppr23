package org.texttechnologylab.project.Stud1.Uebung1.models.person;

import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.BiografischeAngaben;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.Name;

/**
 * Ein Besucher.<br>
 * Enthält alle Daten der Person.
 */
public interface Besucher extends Person {
    /* Empty for now, may get more properties in the future */
}

class BesucherImpl implements Besucher {
    private Name[] namen;
    private BiografischeAngaben biografischeAngaben;
    private String email;

    public BesucherImpl(Name[] namen, BiografischeAngaben biografischeAngaben, String email) {
        this.namen = namen;
        this.biografischeAngaben = biografischeAngaben;
        this.email = email;
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
