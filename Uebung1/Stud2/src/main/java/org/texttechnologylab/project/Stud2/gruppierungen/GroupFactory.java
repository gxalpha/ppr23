package org.texttechnologylab.project.Stud2.gruppierungen;

import org.texttechnologylab.project.Stud2.impl.*;

// This class (factory) is able to initialize Objects of Class Bundestagsverwaltung, Parlament and Fraktion
public class GroupFactory {
    public Bundestagsverwaltung createBundestagsverwaltung(int btv_id, PersonalBundestagsverwaltung[] personal){
        return null;
    }

    public Parlament createParlament(int wahlperiode, Abgeordneter[] abgeordnete, Fraktion[] fraktionen){
        return null;
    }

    public Fraktion createFraktion(int fraktionsID, String name, Abgeordneter[] abgeordnete){
        return null;
    }
}
