package b7.tools.tracking;

import java.util.Comparator;

/**
 * Represents the collection of crawl data
 * for a single website
 */
public class CrawlData {

    // Name of the data being crawled (ex: "Sentai Filmworks Crawler")
    String title;

    // Comparator to use for the TreeMap that holds all the crawl data, so we sort in alphabetical order
    //   while ignoring any case differences that might have caused products to be placed in unexpected places
    public final static Comparator<String> caseInsensitiveComparator = new Comparator<String>() {
        @Override
        public int compare(String string1, String string2) {
            String s1Lowercase = string1.toLowerCase();
            String s2Lowercase = string2.toLowerCase();
            return s1Lowercase.compareTo(s2Lowercase);
        }
    };
}
