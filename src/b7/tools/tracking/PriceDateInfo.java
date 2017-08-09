package b7.tools.tracking;

import java.text.SimpleDateFormat;
import java.util.Date;
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
     * Returns the current startDate String in the format used by this program in general
     * (YYYY-MM-DD)
     * @return String representation of current startDate
     */
    public static String getCurrentDateString() {
        return getDateString(System.currentTimeMillis());
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
        return (int) (this.price - other.price);
    }
}
