package b7.tools.tracking;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Represents the collection of crawl data
 * for a single website
 */
public class CrawlData {

    // Comparator to use for the TreeMap that holds all the crawl data, so we sort in alphabetical order
    //   while ignoring any case differences that might have caused products to be placed in unexpected places
    public final static Comparator<String> CASE_INSENSITIVE_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare(String string1, String string2) {
            String s1Lowercase = string1.toLowerCase();
            String s2Lowercase = string2.toLowerCase();
            return s1Lowercase.compareTo(s2Lowercase);
        }
    };

    // Name of the data being crawled (ex: "Sentai Filmworks Crawler")
    private String title;

    // Structure to hold all the product information, with product title leading to Product details
    private TreeMap<String, Product> productTreeMap;

    /**
     * Creates a new CrawlData
     * @param title the title of the CrawlData
     */
    public CrawlData(String title) {
        this.title = title;
        productTreeMap = new TreeMap<String, Product>(CASE_INSENSITIVE_COMPARATOR);
    }
}
