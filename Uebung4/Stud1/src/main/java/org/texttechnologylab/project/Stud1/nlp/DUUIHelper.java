package org.texttechnologylab.project.Stud1.nlp;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.texttechnologylab.DockerUnifiedUIMAInterface.DUUIComposer;
import org.texttechnologylab.DockerUnifiedUIMAInterface.driver.DUUIDockerDriver;
import org.texttechnologylab.DockerUnifiedUIMAInterface.lua.LuaConsts;
import org.texttechnologylab.project.Stud1.data.Rede;
import org.texttechnologylab.project.Stud1.util.Static;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Helper class to analyze speeches using DUUI.
 * Since we don't want to have multiple of the same Docker image running, so we make sure you CAN NOT create multiple
 * objects of this class.
 * This behaves like the Kotlin "Object" class type by having everything as static methods.
 */
public class DUUIHelper {

    private static final DUUIComposer duuiComposer;
    private static boolean isReady;

    static {
        isReady = false;

        if (Static.ENABLE_NLP) {
            try {
                duuiComposer = new DUUIComposer()
                        .withSkipVerification(true)
                        .withLuaContext(LuaConsts.getJSON())
                        .withWorkers(1)
                        .addDriver(new DUUIDockerDriver())
                        .add(new DUUIDockerDriver.Component("docker.texttechnologylab.org/textimager-duui-spacy-single-de_core_news_sm:0.1.4")
                                .withScale(1)
                                .build())
                        .add(new DUUIDockerDriver.Component("docker.texttechnologylab.org/gervader_duui:1.0.2")
                                .withParameter("selection", "text")
                                .withScale(1)
                                .build());

                new Thread(() -> {
                    try {
                        JCas jCas = JCasFactory.createJCas();
                        jCas.setDocumentText("Dies ist Text, um die Container zu starten.");
                        duuiComposer.run(jCas);
                        isReady = true;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();

            } catch (URISyntaxException | IOException | UIMAException | SAXException | CompressorException e) {
                throw new RuntimeException(e);
            }
        } else {
            duuiComposer = null;
            isReady = true;
        }
    }

    /**
     * Unusable constructor.
     * You must construct additional pylons!
     */
    private DUUIHelper() {
        throw new RuntimeException("No.");
    }

    /**
     * @param rede Die Rede
     * @return JCas der Analyse
     * @throws Exception Wenn Dinge schieflaufen
     */
    public static JCas analyze(Rede rede) throws Exception {
        JCas jCas = rede.toCAS();
        duuiComposer.run(jCas);
        return jCas;
    }

    public static boolean isReady() {
        return isReady;
    }
}
