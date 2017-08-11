package b7.tools.tracking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

/**
 * Simple class to represent a startDate and price combination
 * for Items to use to help create a price history
 */
public class PriceDateInfo implements Comparable<PriceDateInfo> {

    private String startDate;  // When this price started
    private String endDate;  // When this price ended
    private double price;  // The actual price during the date range

    /**
     * Constructs a new PriceDateInfo with today as the startDate and endDate and a price of 0
     */
    public PriceDateInfo() {
        startDate = getCurrentDateString();
        endDate = startDate;
        setPrice(0.0);
    }

    /**
     * Constructs a new PriceDateInfo with today as the startDate and endDate and the specified price
     * @param price the price to set for the PriceDate
     */
    public PriceDateInfo(double price) {
        startDate = getCurrentDateString();
        endDate = startDate;
        setPrice(price);
    }

    public PriceDateInfo(String startDate, double price) {
        setStartDate(startDate);
        endDate = startDate;
        setPrice(price);
    }

    public PriceDateInfo(String startDate, String endDate, double price) {
        setStartDate(startDate);
        setEndDate(endDate);
        setPrice(price);
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public double getPrice() {
        return price;
    }

    public void setStartDate(String newDate) {
        // Make sure the newDate is valid
        String pattern = "^\\d\\d\\d\\d-\\d\\d-\\d\\d$";
        boolean matches = Pattern.matches(pattern, newDate);
        if(newDate != null && matches) {
            startDate = newDate;
        }
        else {
            throw new IllegalArgumentException("Cannot set startDate for PriceDateInfo as null or invalid format!");
        }
    }

    public void setEndDate(String newDate) {
        // Make sure the newDate is valid
        String pattern = "^\\d\\d\\d\\d-\\d\\d-\\d\\d$";
        boolean matches = Pattern.matches(pattern, newDate);
        if(newDate != null && matches && newDate.compareTo(startDate) >= 0) {
            endDate = newDate;
        }
        else {
            throw new IllegalArgumentException("Cannot set endDate for PriceDateInfo as null, " +
                    "an invalid format, or before the startDate!");
        }
    }

    /**
     * Sets the price of the PriceDateInfo to the specified value, assuming
     * the given new price is valid (i.e., not negative). If the price
     * is invalid, the price will be reset to 0
     * @param newPrice the new price to set for this
     */
    public void setPrice(double newPrice) {
        if(newPrice >= 0) {
            price = newPrice;
        }
        else{
            throw new IllegalArgumentException("Cannot set price for PriceDateInfo as less than 0!");
        }
    }

    /**
     * Returns the given dateInMilliseconds in the proper format for the PriceDateInfo
     * @param dateInMilliseconds milliseconds since January 1, 1970, 00:00:00 GMT to get date for
     * @return String representation of date that matches given time
     */
    public static String getDateString(long dateInMilliseconds) {
        // Create date with given milliseconds and use a SimpleDateFormat to get desired format to use (YYYY-MM-DD)
        Date date = new Date(dateInMilliseconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    /**
     * Returns the current date in the format used by this program in general (YYYY-MM-DD)
     * @return String representation of current date in the format YYYY-MM-DD
     */
    public static String getCurrentDateString() {
        return getDateString(System.currentTimeMillis());
    }

    /**
     * Returns the day that comes at an offset to the given date
     * @param date the date to determine the offset day for
     * @param offset the offset (in days) to find the date for (like -4 for 4 days ago, 2 for 2 days ahead, etc.)
     * @return formatted date that comes at the offset to the given date
     */
    public static String getDateOffset(String date, int offset) {
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

        // Use getDateString to find String of next date
        return getDateString(nextDateInMillis);
    }

    /**
     * Returns a basic String representation of the start date, end date, and price of the PriceDateInfo
     * @return a basic String representation of the start date, end date, and price of the PriceDateInfo
     */
    @Override
    public String toString() {
        return (startDate + " through " + endDate + " at price " + price);
    }

    /**
     * Compares this PriceDateInfo to another PriceDateInfo
     * @param other the other PriceDateInfo to compare to
     * @return negative if this PriceDateInfo is less than other (or other is null), positive if this PriceDateInfo is greater
     * than other, and zero if both PriceDateInfo have the same properties
     * Comparing is done (in order of significance): start date (ascending), end date (ascending), price (ascending)
     */
    @Override
    public int compareTo(PriceDateInfo other) {
        if(other == null) {
            return -1;
        }

        // Compare start date
        if(this.startDate.compareTo(other.startDate) < 0) {
            return -1;
        }
        else if(this.startDate.compareTo(other.startDate) > 0) {
            return 1;
        }

        // Compare end date
        if(this.endDate.compareTo(other.endDate) < 0) {
            return -1;
        }
        else if(this.endDate.compareTo(other.endDate) > 0) {
            return 1;
        }

        // Compare price
        double priceDifference = this.price - other.price;
        if(priceDifference < 0) {
            return -1;
        }
        else if(priceDifference > 0) {
            return 1;
        }
        return 0;
    }
}
