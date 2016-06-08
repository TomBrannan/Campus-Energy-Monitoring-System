package utilities;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Level;

/**
 * Class which contains several methods for converting between String and
 * LocalDateTime date representations.
 *
 * @author cws55854
 */
public class LocalDateTimeUtilities {

    private static final String JAVASCIPT_FORM_DATE_TIME_PARSE_FORMAT = "M/d/uuuu h:ma";
    private static final String ENGLISH_DATE_TIME_OUTPUT_FORMAT = "M/d/uuuu h:mma";
    private static final String DATA_REPORT_DATETIME_FORMAT = "M/d/uuuu H:mm";
    public static final DateTimeFormatter JAVASCRIPT_FORM_DATE_TIME_PARSE_FORMATTER = DateTimeFormatter.ofPattern(JAVASCIPT_FORM_DATE_TIME_PARSE_FORMAT); // valid format. Won't throw exception
    public static final DateTimeFormatter ENGLISH_DATE_TIME_OUTPUT_FORMATTER = DateTimeFormatter.ofPattern(ENGLISH_DATE_TIME_OUTPUT_FORMAT);
    public static final DateTimeFormatter DATA_REPORT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATA_REPORT_DATETIME_FORMAT);
    
    /**
     * Round the given LocalDateTime's minute value to the nearest interval value. 
     * Ex: With a minute value of 17 and an interval of 30 minutes, the minute value
     * will be rounded to 30(30*1). With a minute value of 8 and an interval of 3 minutes,
     * the minute value will be rounded to 9(3*3).
     * @param ldt The LocalDateTime to round.
     * @param min_interval Factor by which the minute value is rounded. 
     * @return 
     */
    public static LocalDateTime roundDateTimeMinutes(LocalDateTime ldt, int min_interval)
    {
        if (ldt == null) {
            return null;
        }
        if (min_interval <= 0 || 60 % min_interval != 0) {
            return ldt;
        }
        
        // Truncate to the nearest minute
        ldt = ldt.truncatedTo(ChronoUnit.MINUTES);
        
        // Find the difference
        int minDiff = ldt.getMinute() % min_interval;
        if (minDiff == 0) {
            return ldt;
        }
        
        int diff = min_interval - minDiff;
        if (minDiff < min_interval / 2.0) {
            return ldt.minusMinutes(minDiff);
        }
        else {
            return ldt.plusMinutes(diff);
        }
    }
    
    public static LocalDateTime roundDownDateTimeMinutes(LocalDateTime ldt, int min_interval)
    {
        if (ldt == null) {
            return null;
        }
        if (min_interval <= 0) {
            return ldt;
        }
        
        // Truncate to the nearest minute
        ldt = ldt.truncatedTo(ChronoUnit.MINUTES);
        
        // Find the difference
        int minDiff = ldt.getMinute() % min_interval;
        return ldt.minusMinutes(minDiff);
    }
    
    
    /**
     * Will format the given LocalDateTime using "M/d/uuuu h:mma" format for easy reading.
     * @param ldt The LocalDateTime to convert.
     * @return The string representation of the given LocalDateTime.
     */
    public static String getEnglishDateTimeString(LocalDateTime ldt)
    {
        if (ldt == null) {
            return null;
        }
        return ldt.format(ENGLISH_DATE_TIME_OUTPUT_FORMATTER);
    }
    
    /**
     * Will return the ISO-date-time String representation of the given
     * <code>LocalDateTime</code> with your current Zone information included. 
     *
     * @param ldt The <code>LocalDateTime</code> to get a String representation
     * of.
     * @return the ISO-date-time String representation of the given
     * <code>LocalDateTime</code>.
     */
    public static String getZonedDateTimeString(LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        }
        ZonedDateTime dateTimeWithZone = ldt.atZone(ZoneId.systemDefault());
        return dateTimeWithZone.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * Will return the ISO-date-time String representation of the given
     * <code>LocalDateTime</code>.
     *
     * @param ldt The <code>LocalDateTime</code> to get a String representation
     * of.
     * @return the ISO-date-time String representation of the given
     * <code>LocalDateTime</code>.
     */
    public static String getISODateTimeString(LocalDateTime ldt) {
        return ldt.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * Will convert the given date-time String (format specified by the given
     * <code>DateTimeFormatter</code> into a string using ISODateTime format.
     *
     * @param localDateTimeString String to parse
     * @param formatter <code>DateTimeFormatter</code> used to format the given
     * String.
     * @return If the String has a valid format (specified by the given
     * <code>DateTimeFormatter</code>, a <code>LocalDateTime</code> representing
     * that String is returned. Otherwise a null reference is returned.
     */
    public static String getISODateTimeString(String localDateTimeString, DateTimeFormatter formatter) {
        return getISODateTimeString(parseOtherDateTimeString(localDateTimeString, formatter));
    }

    /**
     * Will return the ISO-date String representation of the given
     * <code>LocalDateTime</code>.
     *
     * @param ldt The <code>LocalDateTime</code> to get a String representation
     * of.
     * @return the ISO-date String representation of the given
     * <code>LocalDateTime</code>.
     */
    public static String getISODateString(LocalDateTime ldt) {
        return ldt.format(DateTimeFormatter.ISO_DATE);
    }

    /**
     * Will return the ISO-time String representation of the given
     * <code>LocalDateTime</code>.
     *
     * @param ldt The <code>LocalDateTime</code> to get a String representation
     * of.
     * @return the ISO-time String representation of the given
     * <code>LocalDateTime</code>.
     */
    public static String getISOTimeString(LocalDateTime ldt) {
        return ldt.format(DateTimeFormatter.ISO_TIME);
    }

    /**
     * Will parse the given String with ISO-date-time format.
     *
     * @param localDateTimeString String to parse
     * @return If the String has valid ISO-date-time format, a
     * <code>LocalDateTime</code> representing that String is returned.
     * Otherwise a null reference is returned.
     */
    public static LocalDateTime parseISODateTimeString(String localDateTimeString) {
        if (localDateTimeString == null) {
            return null;
        }

        LocalDateTime parsedDate = null;
        try {
            parsedDate = LocalDateTime.parse(localDateTimeString);
        } catch (DateTimeParseException ex) {
            ErrorLogger.log(Level.WARNING, "TimeConverter.parseISODateTimeString: Unable to parse as ISO dateTimeString(" + localDateTimeString + ") -- EXCEPTION MESSAGE: " + ex.getMessage());
        }
        return parsedDate;
    }

    /**
     * Will parse the given String with the given
     * <code>DateTimeFormatter</code>.
     *
     * @param localDateTimeString String to parse
     * @param formatter <code>DateTimeFormatter</code> used to format the given
     * String.
     * @return If the String has a valid format (specified by the given
     * <code>DateTimeFormatter</code>, a <code>LocalDateTime</code> representing
     * that String is returned. Otherwise a null reference is returned.
     */
    public static LocalDateTime parseOtherDateTimeString(String localDateTimeString, DateTimeFormatter formatter) {
        LocalDateTime parsedDate = null;
        try {
            parsedDate = LocalDateTime.parse(localDateTimeString, formatter);
        } catch (DateTimeParseException ex) {
            ErrorLogger.log(Level.WARNING, "TimeConverter.parseOtherDateTimeString: Unable to parse dateTimeString(" + localDateTimeString + ") using format(" + formatter + ") -- EXCEPTION MESSAGE: " + ex.getMessage());
        }
        return parsedDate;
    }

    /**
     * Calculate the absolute difference (in minutes) between the two given
     * <code>LocalDateTime</code>'s.
     *
     * @param d1 First time
     * @param d2 Second time
     * @return The absolute difference (in minutes) between the two given
     * <code>LocalDateTime</code>'s, d1 and d2.
     */
    public static int minuteDifference(LocalDateTime d1, LocalDateTime d2) {
        return (int) Math.abs(ChronoUnit.MINUTES.between(d1, d2));
    }

    /**
     * Compares the given <code>LocalDateTime</code>'s to check whether or not
     * <code>between</code> is chronologically between <code>before</code> and
     * <code>after</code>.
     *
     * @param first One of the bounding date-times
     * @param second One of the bounding date-times
     * @param between Date-time to check
     * @return True if <code>between</code> is chronologically between
     * <code>before</code> and <code>after</code>. Otherwise false.
     */
    public static boolean dateTimeIsBetween(LocalDateTime first, LocalDateTime second, LocalDateTime between) {
        return (first.isBefore(between) && second.isAfter(between)) || (first.isAfter(between) && second.isBefore(between));
    }

    public static void main(String[] args) {
        LocalDateTime date1 = LocalDateTime.of(2010, Month.MARCH, 1, 1, 0, 0);

        int interval = 2;
        for(int i = 0; i <= 60; i++) {
            System.out.println(getISOTimeString(date1) + "-> " + getISOTimeString(roundDateTimeMinutes(date1, interval)));
            date1 = date1.plusMinutes(1);
        }
    }

}
