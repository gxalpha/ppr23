package org.texttechnologylab.project.Stud2.data;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.type.VaderSentiment;
import org.texttechnologylab.project.Stud2.database.MongoDBConnectionHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

/**
 * Interface für Objekte, die NLP-Informationen enthalten (Aufgabe 2f)
 *
 * @author Stud2
 */
public interface NLPObject {

    /**
     * @return das Dokument als JCas
     */
    JCas toCAS() throws UIMAException;

    /**
     * @param jCas das neue JCas der Rede
     */
    public void setJCas(JCas jCas);

    /**
     * Löscht das CAS aus dem Objekt, um Speicherplatz zu sparen
     */
    void setCasToNull();

    /**
     * @return eine Liste der Token des Dokumentes
     */
    List<Token> getToken() throws UIMAException;

    /**
     * @return alle Sätze des Dokumentes
     */
    List<Sentence> getSentences() throws UIMAException;

    /**
     * @return das POS des Dokuments
     */
    List<POS> getPOS() throws UIMAException;

    /**
     * @return die Dependencies des Dokumentes
     */
    List<Dependency> getDependency() throws UIMAException;

    /**
     * @return eine Liste der NamedEntities des Dokumentes
     */
    List<NamedEntity> getNamedEntities() throws UIMAException;

    /**
     * @return das Sentiment des Dokumentes sowie der einzelnen Sätze
     */
    List<VaderSentiment> getSentiment() throws UIMAException;

    /**
     * Deserialisiert das CAS der Rede aus der MongoDB und speichert es im Objekt ab
     * → MUSS aufgerufen werden, um mit dem CAS der Rede arbeiten zu können!
     *
     * @param mongoDB der MongoDBConnectionHandler
     */
    void fetchCASFromMongoDB(MongoDBConnectionHandler mongoDB) throws UIMAException, IOException, SAXException;
}
