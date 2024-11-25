package org.texttechnologylab.project.Parliament_Browser_09_2.parsing;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Class containing methods to parse various types of dates and times.
 *
 * @author Stud based on code from Uebung 2-4
 */
public class XMLTools {


    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter alternativeTimeFormat1 = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter alternativeTimeFormat2 = DateTimeFormatter.ofPattern("HH.mm");
    private static final DateTimeFormatter alternativeTimeFormat3 = DateTimeFormatter.ofPattern("H.mm");

    private static final DateTimeFormatter[] timeFormats = new DateTimeFormatter[]{
            TIME_FORMAT, alternativeTimeFormat2, alternativeTimeFormat1, alternativeTimeFormat3
    };


    /**
     * Parse a date in German date format
     *
     * @param date the date as string
     * @return the date as LocalDate object
     */
    public static Date parseDate(String date) throws DateTimeParseException {
        return Date.from(LocalDate.parse(date, XMLTools.DATE_FORMAT).atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    /**
     * Parse a date with month as German text
     *
     * @param date the date
     * @return the date
     * @throws DateTimeParseException
     */
    public static LocalDate parseGermanDate(String date) throws DateTimeParseException {
        if (date.charAt(2) == '.')
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd. MMMM yyyy", Locale.GERMAN));
        else return LocalDate.parse(date, DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.GERMAN));
    }

    /**
     * Parse a time in German time format
     *
     * @param time the time as string
     * @return the time as LocalTime object
     */
    public static LocalTime parseTime(String time) throws DateTimeParseException {
        time = time.replace(" ", "").replace("Uhr", "");
        LocalTime result = null;
        for (int i = 0; i < timeFormats.length; i++) {
            DateTimeFormatter format = timeFormats[i];
            try {
                result = LocalTime.parse(time, format);
                break;
            } catch (DateTimeParseException e) {
                if (i == timeFormats.length - 1) throw e;
            }
        }
        return result;
    }


}
