package org.texttechnologylab.project.Parliament_Browser_09_2.data;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import org.bson.Document;

import java.util.List;

/**
 * NLP-Ergebnisse eines Redeelements
 *
 * @author Stud
 * @author Stud (geändert)
 */
public interface RedeElementNLP {
    /**
     * @return Sentiment des Rede-Elements
     */
    double getSentiment();

    /**
     * @return die POS (Parts of speech)
     */
    List<POS> getPOS();

    /**
     * @return NamedEntities des Rede-Elements
     */
    List<RedeElementNLPNamedEntity> getNamedEntities();

    /**
     * @return Objekt in ihrer MongoDB-Repräsentation
     */
    Document toDoc();
}
