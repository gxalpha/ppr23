package org.texttechnologylab.project.Stud2.data.impl.mongoDB;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.TypeSystemUtil;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hucompute.textimager.uima.type.VaderSentiment;
import org.texttechnologylab.project.Stud2.data.NLPObject;
import org.texttechnologylab.project.Stud2.data.impl.file.RedeImpl;
import org.texttechnologylab.project.Stud2.database.MongoDBConnectionHandler;
import org.texttechnologylab.project.Stud2.exceptions.AbgeordneterNotFoundException;
import org.texttechnologylab.project.Stud2.data.BundestagFactory;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Eine Klasse für Reden, die aus der MongoDB ausgelesen wurden
 *
 * @author Stud2
 */
public class RedeMongoDBImpl extends RedeImpl implements NLPObject {
    private JCas jCas = null;

    /**
     * Konstruktor für ein Objekt der Klasse Rede_MongoDB_Impl
     *
     * @param doc das Dokument, welches die Datenbank für eine Rede liefert
     */
    public RedeMongoDBImpl(Document doc, BundestagFactory factory) throws AbgeordneterNotFoundException {
        super(Integer.parseInt(doc.getString("_id")),
                "ID" + doc.getString("_id"),
                factory.getAbgeordneterByIDFromMongoDB(Integer.parseInt(doc.getString("rednerID"))),
                doc.getString("text"),
                new Date(doc.getDate("datum").getTime()));
    }

    /**
     * @return das Dokument als JCas
     */
    @Override
    public JCas toCAS() throws UIMAException {
        if (this.jCas == null) {
            return JCasFactory.createText(this.getText(), "de");
        }

        return this.jCas;
    }

    /**
     * Löscht das CAS aus dem Objekt, um Speicherplatz zu sparen
     */
    public void setCasToNull() {
        this.jCas = null;
    }

    /**
     * @return eine Liste der Token des Dokumentes
     */
    @Override
    public List<Token> getToken() throws UIMAException {
        return new ArrayList<>(JCasUtil.select(this.toCAS(), Token.class));
    }

    /**
     * @return alle Sätze des Dokumentes
     */
    @Override
    public List<Sentence> getSentences() throws UIMAException {
        return new ArrayList<>(JCasUtil.select(this.toCAS(), Sentence.class));
    }

    /**
     * @return das POS des Dokumentes
     */
    @Override
    public List<POS> getPOS() throws UIMAException {
        return new ArrayList<>(JCasUtil.select(this.toCAS(), POS.class));
    }

    /**
     * @return die Dependencies des Dokumentes
     */
    @Override
    public List<Dependency> getDependency() throws UIMAException {
        return new ArrayList<>(JCasUtil.select(this.toCAS(), Dependency.class));
    }

    /**
     * @return eine Liste der NamedEntities des Dokumentes
     */
    @Override
    public List<NamedEntity> getNamedEntities() throws UIMAException {
        return new ArrayList<>(JCasUtil.select(this.toCAS(), NamedEntity.class));
    }

    /**
     * @return das Sentiment des Dokumentes
     */
    @Override
    public List<VaderSentiment> getSentiment() throws UIMAException {
        return new ArrayList<>(JCasUtil.select(this.toCAS(), VaderSentiment.class));
    }

    /**
     * @param jCas das neue JCas der Rede
     */
    public void setJCas(JCas jCas) {
        this.jCas = jCas;
    }

    /**
     * Deserialisiert das CAS der Rede aus der MongoDB und speichert es im Objekt ab
     * → MUSS aufgerufen werden, um mit dem CAS der Rede arbeiten zu können!
     *
     * @param mongoDB der MongoDBConnectionHandler
     */
    public void fetchCASFromMongoDB(MongoDBConnectionHandler mongoDB) throws UIMAException, IOException, SAXException {

        // Datenbankabfrage
        List<Bson> query = List.of(Aggregates.match(Filters.eq("_id", String.valueOf(this.getID()))));

        Document doc = mongoDB.aggregate(query, "Reden").first();

        // Deserialisierung
        TypeSystemDescription desc = TypeSystemUtil.typeSystem2TypeSystemDescription(this.toCAS().getTypeSystem());

        assert doc != null;

        CAS cas = CasCreationUtils.createCas(desc, null, null, null);

        XmiCasDeserializer.deserialize(new ByteArrayInputStream(doc.getString("cas").getBytes()), cas);

        // jCas dieser Rede updaten
        this.jCas = cas.getJCas();
    }
}
