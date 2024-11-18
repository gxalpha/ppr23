package org.texttechnologylab.project.Stud1.Uebung1.models.person;

import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.BiografischeAngaben;
import org.texttechnologylab.project.Stud1.Uebung1.models.person.stammdaten.Name;

/**
 * Eine Person.<br>
 * Enthält den Namen (oder die Namen), die biografischen Angaben und eine Kontakt-Email-Adresse.
 */
public interface Person {
    public Name[] getNamen();

    public void setNamen(Name[] namen);

    public BiografischeAngaben getBiografischeAngaben();

    public void setBiografischeAngaben(BiografischeAngaben biografischeAngaben);

    public String getEmail();

    public void setEmail(String email);
}
