package org.texttechnologylab.project.Parliament_Browser_09_2.website.impl;

import org.junit.jupiter.api.Test;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Sitzung;
import org.texttechnologylab.project.Parliament_Browser_09_2.data.Tagesordnungspunkt;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.MongoDBHandler;
import org.texttechnologylab.project.Parliament_Browser_09_2.database.impl.MongoDBHandlerImpl;
import org.texttechnologylab.project.Parliament_Browser_09_2.website.ProtokolToLaTeX;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Klasse, die Protkolle in LaTeX in PDF umwandeln kann
 *
 * @author Stud
 */
public class ProtokolToLaTeXImpl implements ProtokolToLaTeX {
    /**
     * wandle Protokoll in LaTeX um
     *
     * @param id Id des Protokolls, das zu einer LaTeX-Datei werden soll
     * @author Stud
     */
    @Override
    public void protokollToLaTeX(String id) throws IOException {
        MongoDBHandler mongoDBHandler = new MongoDBHandlerImpl("Project_09_02.txt");
        Sitzung protokoll = mongoDBHandler.getSitzungByID(id);

        String filename = "tmp/latex/" + id + ".tex";

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename));

            String texCode = "";

            texCode += "\\documentclass{article}\n";
            texCode += "\\usepackage{graphicx}\n";
            texCode += "\\usepackage{float}\n";
            texCode += "\\usepackage[T1]{fontenc}\n";
            texCode += "\\usepackage{uspace}\n\n";

            texCode += "\\title{Protkoll von der Sitzung " + protokoll.getSitzungsnummer() + ", WP " + protokoll.getWahlperiode() + "}\n";
            texCode += "\\author{Insight Bundestag 2.0}\n";
            //Todo: Datum einfügen
            texCode += "\n";

            texCode += "\\begin{document}\n\n";

            texCode += "\\maketitle\n\n";

            texCode += "\\tableofcontents\n\n";

            writer.println(texCode);

            for (Tagesordnungspunkt tagesordnungspunkt : protokoll.getTagesordnungspunkte()) {
                texCode = "";
                texCode += tagesordnungspunkt.toTeX();
                texCode += "\n\n";
                writer.println(texCode);
                writer.flush();
            }

            texCode = "\\end{document}\n\n";

            writer.println(texCode);

            writer.close();

        } catch (IOException e) {
            System.err.println("Fehler beim Erstellen der LaTeX-Datei: " + e.getMessage());
        }

    }

    /**
     * wandle LaTeX in PDF um
     *
     * @author Stud
     * @author Stud (Änderungen)
     */
    @Override
    public boolean laTeXToPDF(String id) throws IOException {

        String filePath = "tmp/latex/";
        File file = new File(filePath);
        if (file.isFile()) {
            file.delete();
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"pdflatex", "-synctex=1", "-interaction=nonstopmode", "-output-directory=" + filePath, filePath + id + ".tex"});

            process.waitFor();
            return false;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * wandelt Sitzung in PDF um
     *
     * @param sitzungsId Id der sitzung, die umgwandelt werden soll
     * @throws IOException
     * @author Stud
     */
    public static void createLatexUndPdf(String sitzungsId) throws IOException {
        ProtokolToLaTeX protokolToLaTeX = new ProtokolToLaTeXImpl();

        protokolToLaTeX.protokollToLaTeX(sitzungsId);

        protokolToLaTeX.laTeXToPDF(sitzungsId);
        protokolToLaTeX.laTeXToPDF(sitzungsId);
    }

    /**
     * Testet umwandlung von Protokoll in Latex
     *
     * @throws IOException
     */
    @Test
    public void testLatexToPDF() throws IOException {
        ProtokolToLaTeX protokolToLaTeX = new ProtokolToLaTeXImpl();

        protokolToLaTeX.protokollToLaTeX("WP19-1");

        protokolToLaTeX.laTeXToPDF("WP19-1");
    }

    /**
     * Testet Umwandlung von Latex zu PDF
     *
     * @throws IOException
     */
    @Test
    public void testPdfKonvertierung() throws IOException {
        ProtokolToLaTeX protokolToLaTeX = new ProtokolToLaTeXImpl();

        protokolToLaTeX.laTeXToPDF("WP19-1");
    }
}
