package org.texttechnologylab.project.Stud2.data;

import org.bson.Document;
import org.texttechnologylab.project.Stud2.exception.RedeNotFoundException;

import java.util.List;

/**
 * Ein Interface für einen Tagesordnungspunkt (inklusive Zusatzpunkten und Einzelplänen)
 *
 * @author Stud2
 */
public interface Tagesordnungspunkt extends BundestagObject {

    /**
     * @param ID die ID der zurückzugebenden Rede
     * @return die gesuchte Rede mit der übergebenen ID
     * @throws RedeNotFoundException falls die Rede mit der ID nicht existiert
     */
    Rede getRedeByID(int ID) throws RedeNotFoundException;

    /**
     * @param angelegenheit das Thema des Tagesordnungspunktes
     */
    void setAngelegenheit(String angelegenheit);

    /**
     * @return das Thema des Tagesordnungspunktes
     */
    String getAngelegenheit();

    /**
     * @return die Reden, die in diesem Tagesordnungspunkt gehalten wurden
     */
    List<Rede> listReden();

    /**
     * @return der Tagesordnungspunkt als Dokument
     */
    Document toDoc();


}
