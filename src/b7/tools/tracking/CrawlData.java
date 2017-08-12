package b7.tools.tracking;

import java.util.*;

/**
 * Represents the collection of crawl data
 * for a single website
 */
public class CrawlData {

    public static final String DEFAULT_TITLE = "Crawl Data";

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

    // Name of the data being crawled (ex: "Sentai Filmworks Crawl Date")
    private String title;

    // Structure to hold all the product information, with product title leading to Product details
    private Map<String, Product> productMap;

    /**
     * Creates a new CrawlData
     * @param title the title of the CrawlData
     */
    public CrawlData(String title) {
        setTitle(title);
        productMap = new TreeMap<String, Product>(CASE_INSENSITIVE_COMPARATOR);
    }

    /**
     * Creates a new CrawlData with an initial product map to use
     * @param title the title of the CrawlData
     * @param initialProductMap a product map to start the Crawl Data with
     */
    public CrawlData(String title, Map<String, Product> initialProductMap) {
        setTitle(title);
        productMap = initialProductMap;
    }

    /**
     * Returns the title of the CrawlDate
     * @return the title of the CrawlData
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the CrawlData to the newTitle
     * @param newTitle the new title to give to the CrawlData
     * @pre newTitle is not null and at least one character (otherwise title becomes DEFAULT_TITLE)
     */
    public void setTitle(String newTitle) {
        if(newTitle == null || newTitle.length() == 0) {
            this.title = DEFAULT_TITLE;
        }
        else {
            this.title = newTitle;
        }
    }

    /**
     * Returns a copy of the data in the product map for this CrawlData
     * @return a copy of the data in the product map for this CrawlData
     */
    public Map<String, Product> getProductMap() {
        Map<String, Product> productMapClone = new TreeMap<String, Product>(CASE_INSENSITIVE_COMPARATOR);

        Set<String> productMapKeys = productMap.keySet();
        for(String key : productMapKeys) {
            Product currentProduct = productMap.get(key);
            productMapClone.put(key,
                    new Product(currentProduct.getProductName(),
                            currentProduct.getProductURL(),
                            currentProduct.getPriceHistory()));
        }

        return productMapClone;
    }

    /**
     * Adds a Product to the CrawlData (or will update an existing Product with the same name)
     * @param product the Product to add to the data
     */
    public void addProduct(Product product) {
        if(product == null) {
            return;
        }

        Product existingProduct = productMap.get(product.getProductName());
        if(existingProduct == null) {  // Brand new product, so we can quickly add
            productMap.put(product.getProductName(), product);
        }
        else {  // Product with same name already exists
            // Specifically, overwrite existing URL with incoming one and merge the product history
            //   of the existing product with the product to be added, then put the final result
            //   back into the product map
            existingProduct.setProductURL(product.getProductURL());
            product.sortPriceHistory();
            List<PriceDateInfo> newProductPriceHistory = product.getPriceHistory();
            for(PriceDateInfo priceDateInfo : newProductPriceHistory) {
                existingProduct.addNewPriceDateInfo(priceDateInfo);
            }
            productMap.put(product.getProductName(), existingProduct);
        }
    }
}
