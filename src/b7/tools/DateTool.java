package b7.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

/**
 * Tool to convert date strings into LocalDate objects
 */
public final class DateTool {

    /**
     * The regex pattern matching an acceptable date string
     */
    public static final String DATE_STRING_PATTERN = "^\\d+-\\d{2}-\\d{2}$";

    /**
     * Creates a LocalDate object representative of the given date string
     * @param dateString date in the format acceptable by DATE_STRING_PATTERN to convert to LocalDate
     * @return LocalDate that matches the year / month / day of the date string, or null if the given
     * date string is an invalid format
     */
    public static LocalDate getLocalDateFromDateString(String dateString) {
        if(isValidDateString(dateString)) {
            String[] splitDateArray = dateString.split("-");

            int year = Integer.parseInt(splitDateArray[0]);
            int month = Integer.parseInt(splitDateArray[1]);
            int day = Integer.parseInt(splitDateArray[2]);

            return LocalDate.of(year, month, day);
        }
        return null;
    }

    /**
     * Determines if the given date string matches the DATE_STRING_PATTERN or not
     * @param dateString the date string to check for validity
     * @return true if the DATE_STRING_PATTERN matches the date string; false otherwise
      */
    public static boolean isValidDateString(String dateString) {
        return Pattern.matches(DATE_STRING_PATTERN, dateString);
    }

    /**
     * Gets the difference in two LocalDates exclusive (so, if both dates were same day, result is 0)
     * @param startDate the date to start from
     * @param endDate the date to stop at
     * @return the number of days that differ between the start date and end date (exclusive)
     */
    public static int findDifferenceInLocalDatesInDaysExclusive(LocalDate startDate, LocalDate endDate) {
        if(startDate == null || endDate == null) {
            return -1;
        }
        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Gets the difference in two LocalDates inclusive (so, if both dates were same day, result is 1)
     * @param startDate the date to start from
     * @param endDate the date to stop at
     * @return the number of days that differ between the start date and end date (inclusive)
     */
    public static int getDifferenceInLocalDatesInDaysInclusive(LocalDate startDate, LocalDate endDate) {
        return findDifferenceInLocalDatesInDaysExclusive(startDate, endDate) + 1;
    }

    /**
     * Returns the given dateInMilliseconds in the proper format for the PriceDateInfo
     * @param dateInMilliseconds milliseconds since January 1, 1970, 00:00:00 GMT to get date for
     * @return String representation of date that matches given time
     */
    public static String findDateString(long dateInMilliseconds) {
        // Create date with given milliseconds and use a SimpleDateFormat to get desired format to use (YYYY-MM-DD)
        Date date = new Date(dateInMilliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    /**
     * Takes a date string (in proper format YYYY-MM-DD) and gives back the time
     * in milliseconds for the beginning of that date
     * @param dateString the date string to find the milliseconds of
     * @return the milliseconds of the given date if it is a valid format, or -1 if the date string could
     * not be parsed
     */
    public static long findMillisFromDateString(String dateString) {
        // Make sure the date string is valid
        String pattern = "^\\d\\d\\d\\d-\\d\\d-\\d\\d$";
        boolean matches = Pattern.matches(pattern, dateString);
        if(dateString == null || !matches) {
            return -1;
        }

        // Create date with given date string and use a SimpleDateFormat to convert it into a date object,
        //   and then use that date object to get the time in milliseconds for the date string
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(dateString);
            return date.getTime();
        }
        catch(ParseException ex) {
            // Uncomment below to print error statement on parse exception
            //System.err.println("[ERROR] Could not parse given date string " + dateString);
            //ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Returns the current date in the format used by this program in general (YYYY-MM-DD)
     * @return String representation of current date in the format YYYY-MM-DD
     */
    public static String findCurrentDateString() {
        return findDateString(System.currentTimeMillis());
    }

    /**
     * Returns the day that comes at an offset to the given date
     * @param date the date to determine the offset day for
     * @param offset the offset (in days) to find the date for (like -4 for 4 days ago, 2 for 2 days ahead, etc.)
     * @return formatted date that comes at the offset to the given date
     */
    public static String findDateOffset(String date, int offset) {
        // Make sure the newDate is valid
        String pattern = "^\\d\\d\\d\\d-\\d\\d-\\d\\d$";
        boolean matches = Pattern.matches(pattern, date);
        if(date == null || !matches) {
            return null;
        }

        // Split the given day to extract year / month / day as integers
        String[] splitDay = date.split("-");
        int dateYear = Integer.parseInt(splitDay[0]);
        int dateMonth = Integer.parseInt(splitDay[1]);
        int dateDay = Integer.parseInt(splitDay[2]);

        // Use the GregorianCalendar class to help us find the next date by converting back to milliseconds
        //   passed Month is dateMonth - 1 because here, constructor takes month and assumes it is zero-based
        //   (i.e., January is 0, not 1 and December is 11, not 12)
        GregorianCalendar gregorianCalendar = new GregorianCalendar(dateYear, dateMonth - 1, dateDay);
        long dateInMillis = gregorianCalendar.getTimeInMillis();
        final long MILLISECONDS_IN_A_DAY = 1000L * 60 * 60 * 24;  // 1000 ms/s * 60 s/m * 60 m/h * 24 h/day
        long nextDateInMillis = dateInMillis + (MILLISECONDS_IN_A_DAY * offset);

        // Use findDateString to find String of next date
        return findDateString(nextDateInMillis);
    }
}
