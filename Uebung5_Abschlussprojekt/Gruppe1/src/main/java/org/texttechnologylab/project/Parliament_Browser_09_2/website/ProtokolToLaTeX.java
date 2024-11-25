package org.texttechnologylab.project.Parliament_Browser_09_2.website;

import java.io.IOException;

/**
 * Interface, um Protokolle zu LaTeX und dann zu PDF-Dokumenten zu machen
 *
 * @author Stud
 */
public interface ProtokolToLaTeX {

    /**
     * wandle Protokoll in LaTeX um
     *
     * @param id Id des Protokolls, das zu einer LaTeX-Datei werden soll
     * @author Stud
     */
    void protokollToLaTeX(String id) throws IOException;

    /**
     * wandle LaTeX in PDF um
     *
     * @param id ID des protocols
     * @author Stud
     */
    boolean laTeXToPDF(String id) throws IOException;

}
