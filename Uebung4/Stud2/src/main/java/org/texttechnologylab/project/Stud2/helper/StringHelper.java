package org.texttechnologylab.project.Stud2.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Enthält nützliche String-Funktionen
 *
 * @author Stud2
 */
public class StringHelper {
    /**
     * Vereinfacht einen String, indem alle Zeichen, die nicht im 26er-Alphabet sind, gelöscht werden.
     * Titel werden ebenfalls entfernt.
     *
     * @param s der zu vereinfachende String
     * @return der vereinfachte String, der nur Groß- und Kleinbuchstaben des 26er-Alphabets enthält
     */
    public static String simplify(String s) {

        s = s.trim();

        // Alphabet von A-Z (bzw. a-z) erzeugen
        Set<Character> alphabet = new HashSet<>();

        for (int i = 97; i < 123; i++) {
            alphabet.add((char) i);
        }

        for (int i = 65; i < 91; i++) {
            alphabet.add((char) i);
        }

        if (s.startsWith("Dr. h. c. ")) {
            s = s.substring(10);
        } else if (s.startsWith("Dr. ") || s.startsWith("Dr. ")) {
            s = s.substring(4);
        } else if (s.startsWith("Dr.-Ing. ")) {
            s = s.substring(9);
        }

        // Alle Buchstaben aus s entfernen, die nicht im Alphabet enthalten sind
        StringBuilder result = new StringBuilder();

        for (char c : s.toCharArray()) {
            if (String.valueOf(c).equals(" ")) {
                break;
            } else if (alphabet.contains(c)) {
                result.append(c);
            }
        }
        return String.valueOf(result).toUpperCase();
    }

    /**
     * Castet einen String zu einer natürlichen Zahl
     *
     * @param str die einzulesende natürliche Zahl
     * @return der umgewandelte String; -1, falls keine natürliche Zahl übergeben
     */
    public static int toNaturalNumber(String str) throws ClassCastException {
        int result = -1;
        try {
            if (!str.isEmpty()) {
                if (!str.contains(".")) {
                    result = Integer.parseInt(str);
                } else {
                    result = (int) Float.parseFloat(str);
                }
            }
        } catch (NumberFormatException e) {
            throw new ClassCastException("Der übergebene String kann nicht zu einer natürlichen Zahl umgewandelt werden");
        }
        return result;
    }

    /**
     * Castet einen String der Form dd.MM.yyyy zu einem java.sql Date
     *
     * @param date Das umzuwandelnde Datum
     * @return Das Datum vom Typ java.sql. Date
     */
    public static java.sql.Date toDate(String date) throws ParseException {
        if (date.isEmpty()) {
            return null;
        }
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        java.util.Date utilDate = format.parse(date);
        return new java.sql.Date(utilDate.getTime());
    }

    /**
     * Castet einen String der Form dd.MM.yyyy sowie eine Uhrzeit der Form HH:mm zu einem java.sql Date
     *
     * @param date das umzuwandelnde Datum
     * @param time die umzuwandelnde Zeit
     * @return Das Datum mit der Uhrzeit vom Typ java.sql. Date
     */
    public static java.sql.Date toDateTime(String date, String time) throws ParseException {
        if (date.isEmpty() || time.isEmpty()) {
            return null;
        }

        // Falls die 0 vorne fehlen sollte
        if (time.length() == 4) {
            time = "0" + time;
        }

        String dateTime = date.trim() + " " + time.trim();
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        java.util.Date utilDate = format.parse(dateTime);
        return new java.sql.Date(utilDate.getTime());
    }

    /**
     * @return das Datum und die aktuelle Zeit - schön formatiert
     */
    public static String getCurrDateTimeFormatted() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return "[" + LocalDateTime.now().format(format) + "]";
    }

    /**
     * @param start die Startzeit in ms
     * @param end   die Endzeit in ms
     * @return die Zeitdifferenz im Format 'HH:mm:ss'
     */
    public static String getPrettyDuration(long start, long end) {
        long duration = end - start;

        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
