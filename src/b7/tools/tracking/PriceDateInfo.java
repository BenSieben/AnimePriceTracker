package b7.tools.tracking;

import b7.tools.DateTool;

import java.text.ParseException;
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
        startDate = DateTool.findCurrentDateString();
        endDate = startDate;
        setPrice(0.0);
    }

    /**
     * Constructs a new PriceDateInfo with today as the startDate and endDate and the specified price
     * @param price the price to set for the PriceDate
     */
    public PriceDateInfo(double price) {
        startDate = DateTool.findCurrentDateString();
        endDate = startDate;
        setPrice(price);
    }

    /**
     * Constructs a new PriceDateInfo with the start date / end date on the same day and the specified price
     * @param startDate the day to use for the start date / end date of the info
     * @param price the price to use for the info
     */
    public PriceDateInfo(String startDate, double price) {
        setStartDate(startDate);
        endDate = startDate;
        setPrice(price);
    }

    /**
     * Constructs a new PriceDateInfo object
     * @param startDate the start date of the info
     * @param endDate the end date of the info
     * @param price the price during this period of time
     */
    public PriceDateInfo(String startDate, String endDate, double price) {
        setStartDate(startDate);
        setEndDate(endDate);
        setPrice(price);
    }

    /**
     * Returns the current start date of the price date info
     * @return the current start date of the price date info
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the price date info to the given newDate (assuming it is valid)
     * @param newDate the new date to use as the start date
     */
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

    /**
     * Returns the current end date of the price date info
     * @return the current end date of the price date info
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the price date info to the given newDate (assuming it is valid)
     * @param newDate the new date to use as the end date
     */
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
     * Returns the price of this price date info
     * @return the price of this price date info
     */
    public double getPrice() {
        return price;
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
     * Returns a formatted version of the price to always include two decimal places and (optional) currency in front
     * of the value
     * @param currency currency sign (optional - set to null / empty string if not desired)
     * @return the formatted price
     */
    public String formattedPrice(String currency) {
        if(currency == null || currency.length() == 0) {
            return String.format("%.2f", price);
        }
        else {
            return String.format("%s %.2f", currency, price);
        }
    }



    /**
     * Returns a basic String representation of the start date, end date, and price of the PriceDateInfo
     * @return a basic String representation of the start date, end date, and price of the PriceDateInfo
     */
    @Override
    public String toString() {
        return (startDate + " through " + endDate + " at price " + formattedPrice(null));
    }

    /**
     * Returns true if this PriceDateInfo is equivalent to the other object (false otherwise)
     * @param o Object to test against (should be a PriceDateInfo object)
     * @return true if both objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if(o instanceof PriceDateInfo) {
            return this.compareTo((PriceDateInfo)o) == 0;
        }
        return false;
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
