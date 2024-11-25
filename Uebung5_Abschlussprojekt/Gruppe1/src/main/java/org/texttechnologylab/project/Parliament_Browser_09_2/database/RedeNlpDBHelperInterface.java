package org.texttechnologylab.project.Parliament_Browser_09_2.database;

import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElement;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElementNLP;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElementNLPNamedEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface, das NamedEntities und das Sentiment einer Rede aus der Datenbank holt.
 *
 * @author Stud
 */
public interface RedeNlpDBHelperInterface {

    /**
     * Formatiert die Rede als HTML
     *
     * @param rede die Rede
     * @return den Text der Rede als Liste von Strings mit Sentiment etc. hinten dran
     * @author Stud
     * @author Stud
     */
    List<String> getFullText(Rede rede);
}
