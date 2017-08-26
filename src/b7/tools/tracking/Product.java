package b7.tools.tracking;

import b7.tools.DateTool;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A data collection for a single product being tracked,
 * which has price history and basic identifiers
 */
public class Product {

    private String productName;  // Name of the product
    private String productURL;  // URL that accesses the page where you can buy the product
    private List<PriceDateInfo> priceHistory;  // List of all PriceDateInfo objects that compose price history for the product

    // Constants to indicate when bad names / URLs have been assigned to the Product
    public static final String INVALID_NAME = "INVALID_NAME";
    public static final String INVALID_URL = "INVALID_URL";

    /**
     * Constructs a product with name INVALID_NAME, url INVALID_URL, and empty price history
     */
    public Product() {
        this(INVALID_NAME, INVALID_URL);
    }

    /**
     * Constructs a new Product with empty price history
     * @param productName the name of the product
     * @param productURL the URL of the product page
     */
    public Product(String productName, String productURL) {
        setProductName(productName);
        setProductURL(productURL);
        priceHistory = new ArrayList<PriceDateInfo>();
    }

    /**
     * Constructs a new Product with given price history
     * @param productName the name of the product
     * @param productURL the URL of the product page
     * @param priceHistory the price history of the product
     */
    public Product(String productName, String productURL, List<PriceDateInfo> priceHistory) {
        setProductName(productName);
        setProductURL(productURL);
        this.priceHistory = priceHistory;
        sortPriceHistory();
    }

    /**
     * Runs a sort on the current priceHistory
     */
    public void sortPriceHistory() {
        Collections.sort(priceHistory);
    }

    /**
     * Adds a new PriceDateInfo to the priceHistory (or merges it with last entry
     * if the price is the same, by modifying endDate of pre-existing element in priceHistory)
     * @param newPriceDateInfo the new PriceDateInfo to add to the price history of the Product
     */
    public void addNewPriceDateInfo(PriceDateInfo newPriceDateInfo) {
        if(newPriceDateInfo == null) {  // Bad object passed
            return;
        }
        if(priceHistory.size() == 0) {  // Price History is empty, so just add the new info
            priceHistory.add(newPriceDateInfo);
            return;
        }

        // Merge the newPriceDateInfo with the last entry in priceHistory
        PriceDateInfo latestPriceDateInfo = priceHistory.get(priceHistory.size() - 1);
        List<PriceDateInfo> finalPriceDateInfos = mergePriceDateInfos(latestPriceDateInfo, newPriceDateInfo);

        // Remove the latest entry from priceHistory, then add everything in finalPriceDateInfos
        priceHistory.remove(priceHistory.size() - 1);
        priceHistory.addAll(finalPriceDateInfos);
    }

    /**
     * Returns proper result of merging p1 and p2
     * @param p1 the "smaller" PriceDateInfo object to merge
     * @param p2 the "larger" PriceDateInfo object to merge
     * @return the PriceDateInfo merge results as a List (as there can be one or more objects as the merge result)
     */
    private List<PriceDateInfo> mergePriceDateInfos(PriceDateInfo p1, PriceDateInfo p2) {
        List<PriceDateInfo> mergedPriceDateList = new ArrayList<PriceDateInfo>();
        if(p1.compareTo(p2) == 0) {  // If p1 and p2 are the same, we just return p1
            mergedPriceDateList.add(p1);
            return mergedPriceDateList;
        }

        if(p1.compareTo(p2) > 0) {  // Call this method in reverse if user passed arguments in "swapped" order
            return mergePriceDateInfos(p2, p1);
        }

        // Down here, we utilize the knowledge that p1.compareTo(p2) MUST be less than 0 (thanks to if-checks above)
        // First compare start dates
        int startDateComparison = p1.getStartDate().compareTo(p2.getStartDate());
        if(startDateComparison == 0) {  // same start date
            int endDateComparison = p1.getEndDate().compareTo(p2.getEndDate());
            if(endDateComparison == 0) {  // p1 ended same as p2 (same start)
                // p1 must cost less than p2 in this case (same dates and negative comparison), so just return p1
                mergedPriceDateList.add(p1);
                return mergedPriceDateList;
            }
            else if(endDateComparison < 0) {  // p1 ended before p2 (same start)
                // p1 ends before p2 ends (same start), so we must compare prices to determine what to do here
                double priceComparison = p1.getPrice() - p2.getPrice();
                if(Math.abs(priceComparison) <= 0.00000000001) {  // p1 is essentially same cost as p2
                    // set p1's end date to p2's end date and return that single element
                    p1.setEndDate(p2.getEndDate());
                    mergedPriceDateList.add(p1);
                }
                else {  // p1 is cheaper or more expensive than p2 (we do same thing in both situations)
                    // set p2's start date to a day after p1's end date to resolve duplicate entries on same date(s)
                    p2.setStartDate(DateTool.findDateOffset(p1.getEndDate(), 1));
                    mergedPriceDateList.add(p1);
                    mergedPriceDateList.add(p2);
                }
                return mergedPriceDateList;
            }
        }
        else {  // p1 must have started before p2 (if it started after, p1.compareTo(p2) would not be negative)

            // First compare the price (if  price is same, we can just "merge" the two PriceDateInfo)
            double priceComparison = p1.getPrice() - p2.getPrice();
            if(Math.abs(priceComparison) <= 0.00000000001) {  // p1 is essentially same cost as p2
                String mergeStartDate = findMinStartDate(p1, p2);
                String mergeEndDate = findMaxEndDate(p1, p2);
                double mergePrice = p1.getPrice();
                PriceDateInfo mergeInfo = new PriceDateInfo(mergeStartDate, mergeEndDate, mergePrice);
                mergedPriceDateList.add(mergeInfo);
                return mergedPriceDateList;
            }

            // Compare p1.end to p2.end to determine what to return back in merge results
            int p1EndVSP2End = p1.getEndDate().compareTo(p2.getEndDate());
            if(p1EndVSP2End < 0) {  // p1 ends before p2 ends
                // move p1's end date to just before p2's start date to fix any date gap
                p1.setEndDate(DateTool.findDateOffset(p2.getStartDate(), -1));
                mergedPriceDateList.add(p1);
                mergedPriceDateList.add(p2);
            }
            else if(p1EndVSP2End == 0) {  // p1 ends same day as p2 ends
                // move p1's end date to just before p2's start date to fix duplicate date data
                p1.setEndDate(DateTool.findDateOffset(p2.getStartDate(), -1));
                mergedPriceDateList.add(p1);
                mergedPriceDateList.add(p2);
            }
            else {  // p1 ends after p2 ends
                // if p1 ends after p2 ends, then we make three PriceDateInfo objects
                //   to fit p2 in the middle of p1 but preserve p1's part that comes after p2
                String originalP1EndDate = p1.getEndDate();
                p1.setEndDate(DateTool.findDateOffset(p2.getStartDate(), -1));
                PriceDateInfo p3 = new PriceDateInfo(DateTool.findDateOffset(p2.getEndDate(), 1),
                        originalP1EndDate, p1.getPrice());
                mergedPriceDateList.add(p1);
                mergedPriceDateList.add(p2);
                mergedPriceDateList.add(p3);
            }
            return mergedPriceDateList;
        }
        return mergedPriceDateList;  // Technically impossible to get here, but needed to satisfy compiler
    }

    /**
     * Finds lowest price amongst all PriceDateInfo objects
     * in this Product's price history
     * @return the lowest price recorded for this Product (returns 0 if price history is empty)
     */
    public double findLowestPrice() {
        if(priceHistory.size() == 0) {
            return 0;
        }

        double lowestPrice = priceHistory.get(0).getPrice();
        for (int i = 1; i < priceHistory.size(); i++) {
            if(lowestPrice > priceHistory.get(i).getPrice()) {
                lowestPrice = priceHistory.get(i).getPrice();
            }
        }
        return lowestPrice;
    }

    /**
     * Finds highest price amongst all PriceDateInfo objects
     * in this Product's price history
     * @return the highest price recorded for this Product (returns 0 if price history is empty)
     */
    public double findHighestPrice() {
        if(priceHistory.size() == 0) {
            return 0;
        }

        double highestPrice = priceHistory.get(0).getPrice();
        for (int i = 1; i < priceHistory.size(); i++) {
            if(highestPrice < priceHistory.get(i).getPrice()) {
                highestPrice = priceHistory.get(i).getPrice();
            }
        }
        return highestPrice;
    }

    // Helper method to quickly get the lower start date between two PriceDateInfo objects
    private String findMinStartDate(PriceDateInfo p1, PriceDateInfo p2) {
        if(p1.getStartDate().compareTo(p2.getStartDate()) <= 0) {
            return p1.getStartDate();
        }
        return p2.getStartDate();
    }

    // Helper method to quickly get the higher end date between two PriceDateInfo objects
    private String findMaxEndDate(PriceDateInfo p1, PriceDateInfo p2) {
        if(p1.getEndDate().compareTo(p2.getEndDate()) >= 0) {
            return p1.getEndDate();
        }
        return p2.getEndDate();
    }

    /**
     * Returns the product name
     * @return the product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the product name for this Product to a new name
     * @param newProductName the new name to give to the Product
     * @pre newProductName is a valid String that has at least 1 character (otherwise product name becomes INVALID_NAME)
     */
    public void setProductName(String newProductName) {
        if(newProductName != null && newProductName.length() > 0) {
            this.productName = newProductName;
        }
        else {
            this.productName = INVALID_NAME;
        }
    }

    /**
     * Returns the product URL
     * @return the product URL
     */
    public String getProductURL() {
        return productURL;
    }

    /**
     * Sets the product URL for this Product to a new name
     * @param newProductURL the new URL to give to the Product
     * @pre newProductURL is a valid URL (otherwise product URL becomes INVALID_URL)
     */
    public void setProductURL(String newProductURL) {
        if(newProductURL != null && newProductURL.length() > 0) {
            // Try to make URL with the given URL (if we get exception, we know the URL is bad)
            try {
                URL url = new URL(newProductURL);
                this.productURL = newProductURL;
            }
            catch(MalformedURLException ex) {
                this.productURL = INVALID_URL;
            }
        }
    }

    /**
     * Returns a clone of the current priceHistory list
     * @return a clone of the current priceHistory list
     */
    public List<PriceDateInfo> getPriceHistory() {
        // Create a clone of the priceHistory so it cannot be modified outside this class
        List<PriceDateInfo> priceHistoryClone = new ArrayList<PriceDateInfo>(priceHistory.size());
        for (int i = 0; i < priceHistory.size(); i++) {
            PriceDateInfo pdi = priceHistory.get(i);
            priceHistoryClone.add(new PriceDateInfo(pdi.getStartDate(), pdi.getEndDate(), pdi.getPrice()));
        }

        return priceHistoryClone;
        //return priceHistory;
    }

    /**
     * Sets the price history to the given argument
     * @param priceHistory the priceHistory to use for this Product
     */
    public void setPriceHistory(List<PriceDateInfo> priceHistory) {
        this.priceHistory = priceHistory;
    }

    /**
     * Returns the most recent PriceDateInfo entry in the price history list (null if empty)
     * @return the most recent PriceDateInfo entry in the price history list (null if empty)
     */
    public PriceDateInfo findLatestPriceDateInfo() {
        if(priceHistory.size() == 0) {
            return null;
        }
        sortPriceHistory();
        return priceHistory.get(priceHistory.size() - 1);
    }

    /**
     * Returns the PriceDateInfo in the priceHistory with the lowest price. If there are multiple
     * occurrences of the same lowest price, the latest entry is the one that is returned
     * @return lowest price PriceDateInfo in the priceHistory (null if there is nothing in price history)
     */
    public PriceDateInfo findLowestPricePriceDateInfo() {
        if(priceHistory.size() == 0) {
            return null;
        }
        sortPriceHistory();

        // Find the lowest price value / index in the price history, then return it
        double lowestPrice = Double.MAX_VALUE;
        int lowestPriceIndex = 0;
        for (int i = 0; i < priceHistory.size(); i++) {
            PriceDateInfo currentInfo = priceHistory.get(i);
            if(lowestPrice >= currentInfo.getPrice()) {  // Found new low price / same lowest price again
                lowestPrice = currentInfo.getPrice();
                lowestPriceIndex = i;
            }
        }

        return priceHistory.get(lowestPriceIndex);
    }

    @Override
    public String toString() {
        String result = productName;
        result += " (" + productURL + ")";
        result += " --- product history: " + priceHistory.toString();
        return result;
    }

}
