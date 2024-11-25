package org.texttechnologylab.project.Parliament_Browser_09_2.database.impl;

import com.google.common.collect.Lists;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Rede;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElement;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElementNLP;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.RedeElementNLPNamedEntity;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.RedeNlpDBHelperInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Klasse, die NamedEntities und das Sentiment einer Rede aus der Datenbank holt.
 *
 * @author Stud
 */
public class RedeNlpDBHelper implements RedeNlpDBHelperInterface {


    public RedeNlpDBHelper() {
    }

    /**
     * Formatiert die Rede als HTML
     *
     * @param rede die Rede
     * @return den Text der Rede als Liste von Strings mit Sentiment etc. hinten dran
     * @author Stud
     * @author Stud
     */
    public List<String> getFullText(Rede rede) {
        return rede.getRedeElemente().stream().map(redeElement -> {
            StringBuffer buffer = new StringBuffer(redeElement.getText());
            Lists.reverse(redeElement.getNLP().getNamedEntities()).forEach(namedEntity -> {
                String entityCssClass;
                switch (namedEntity.getValue()) {
                    case "PER": {
                        entityCssClass = "highlightPersonen";
                        break;
                    }
                    case "ORG": {
                        entityCssClass = "highlightOrganisationen";
                        break;
                    }
                    case "LOC": {
                        entityCssClass = "highlightLocations";
                        break;
                    }
                    // TODO: There are more types - do we want to differentiate further?
                    default: {
                        return;
                    }
                }
                buffer.replace(namedEntity.getBegin(), namedEntity.getEnd(), "<span class=\"" + entityCssClass + "\">" + namedEntity.getCoveredText() + "</span>");
            });
            float roundingFactor = 100000;
            float roundedSentiment = ((int) (redeElement.getNLP().getSentiment() * roundingFactor)) / roundingFactor;
            buffer.append("<span class=\"sentiment\"> (Sentiment: ").append(roundedSentiment).append(")</span>");
            switch (redeElement.getTyp()) {
                case KOMMENTAR: {
                    buffer.insert(0, "<span class=\"otherspeech\">Kommentar: ");
                    buffer.append("</span>");
                    break;
                }
                case PRAESIDIUM: {
                    buffer.insert(0, "<span class=\"otherspeech\">Präsidium: ");
                    buffer.append("</span>");
                    break;
                }
                case ZWISCHENREDNER: {
                    buffer.insert(0, "<span class=\"otherspeech\">Zwischenredner: ");
                    buffer.append("</span>");
                    break;
                }
            }
            return buffer.toString();
        }).collect(Collectors.toList());
    }


}


