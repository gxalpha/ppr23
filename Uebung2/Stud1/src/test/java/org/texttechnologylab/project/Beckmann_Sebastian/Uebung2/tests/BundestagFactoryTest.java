package org.texttechnologylab.project.Stud1.Uebung2.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.texttechnologylab.project.Stud1.Uebung2.data.BundestagFactory;
import org.texttechnologylab.project.Stud1.Uebung2.exceptions.AbgeordneterNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.texttechnologylab.project.Stud1.Uebung2.tests.TestHelper.*;

@DisplayName("Tests für die BundestagFactory")
public class BundestagFactoryTest {

    @Test
    @DisplayName("Abgeordnete einlesen")
    void testAbgeordneteEinlesen() {
        BundestagFactory factory = BundestagFactory.newInstance();
        assertDoesNotThrow(() -> readStammdaten(factory));
        assertEquals(4376, factory.listAbgeordnete().size(), "Es sind nicht alle oder mehr als erwartete Abgeordnete eingelesen worden.");
        assertEquals(20, factory.listWahlperioden().size(), "Es ist eine falsche Anzahl an Wahlperioden eingelesen worden, momentan sollte es genau 20 geben.");
    }

    @Test
    @DisplayName("Protokolle vor Abgeordneten einlesen")
    void testRedenFirst() {
        BundestagFactory factory = BundestagFactory.newInstance();
        assertThrowsExactly(AbgeordneterNotFoundException.class, () -> readProtokolle(factory));
    }

    @Test
    @DisplayName("Reden vor Abgeordneten einlesen")
    void testAbstimmungenFirst() {
        BundestagFactory factory = BundestagFactory.newInstance();
        assertThrowsExactly(AbgeordneterNotFoundException.class, () -> readAbstimmungen(factory));
    }
}
