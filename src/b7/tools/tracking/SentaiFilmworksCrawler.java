package b7.tools.tracking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * WebCrawler that is specifically customized for the Anime
 * listings on Sentai Filmwork's online store
 */
public class SentaiFilmworksCrawler extends WebCrawler {

    // The base URL to crawl from, not including query parameters
    public final static String BASE_URL = "https://shop.sentaifilmworks.com/collections/shows";

    // The URL to start crawling from
    public final static String INITIAL_URL = BASE_URL + getUrlQuery(1);

    // The base URL of the website (to resolve relative links to the proper path)
    public final static String STORE_URL = "https://shop.sentaifilmworks.com";

    // Certain qualifiers used to help us search through product listing pages for relevant information to extract
    public final static String PRODUCT_CLASS = "home-featured-products";
    public final static String PRODUCT_INFO_ID = "product-info";
    public final static String PRODUCT_TITLE_CLASS = "prod-title";
    public final static String PRODUCT_PRICE_CLASS = "prod-price";
    public final static String PAGINATION_ID = "pagination";
    public final static String NEXT_PAGE_HTML = "&gt;";

    // Qualifiers to help search through product listing pages for relevant information
    public final static String PRODUCTS_ID = "product-loop";
    public final static String PRODUCT_JSON_START_STRING = "var product = ";
    public final static String PRODUCT_JSON_END_STRING = "$('#product-select-'";
    public final static String PRODUCT_JSON_VARIANTS_KEY = "variants";
    public final static String PRODUCT_JSON_URL_COMPONENT_KEY = "handle";
    public final static String PRODUCT_BASE_URL = BASE_URL + "/products";
    public final static String PRODUCT_TITLE_KEY = "title";
    public final static String PRODUCT_VARIANT_KEY = "title";
    public final static String PRODUCT_PRICE_KEY = "price";
    public final static double PRODUCT_PRICE_MULTIPLIER = 100.0;

    // Certain qualifiers used to help us search through product description pages for relevant information
    public final static String PRODUCT_DESCRIPTION_ID = "product-description";
    public final static String PRODUCT_FORMAT_SELECTOR_CLASS = "x-second";
    public final static String PRODUCT_PRICE_ID = "product-price";
    public final static String PRODUCT_CURRENT_PRICE_CLASS = "product-price";
    public final static String PRODUCT_MSRP_CLASS = "was";
    public final static String PRODUCT_VARIATION_QUERY = "?variant=";

    // Path we will save the test base page in (so we can create directory if it doesn't already exist)
    public final static String BASE_PAGE_PATH = "savedata/basepages/";
    // Where we will save the test base page
    public final static String BASE_PAGE_NAME = BASE_PAGE_PATH + "sentaifilmworks.html";

    // Path we will save test product pages at
    public final static String SAMPLE_PRODUCT_PAGES_PATH = "savedata/sampleproductpages/";

    // What descriptor for format we will give to products with just one format (i.e., we do not know if Blu-Ray or DVD)
    public final static String SINGLE_FORMAT_PRODUCT = "N/A";

    public final static String CRAWL_DATA_TITLE = "Sentai Filmworks Crawl Data";
    private CrawlData crawlData;  // The current CrawlData (which we can update with this crawler)


    /**
     * Creates a new SentaiFilmworksCrawler with empty crawl data
     */
    public SentaiFilmworksCrawler() {
        super(INITIAL_URL);
        crawlData = new CrawlData(CRAWL_DATA_TITLE);
    }

    /**
     * Creates a new SentaiFilmworksCrawler with the given initial crawl data
     * @param initialCrawlData the initial CrawlData to use
     */
    public SentaiFilmworksCrawler(CrawlData initialCrawlData) {
        super(INITIAL_URL);
        crawlData = initialCrawlData;
    }

    /**
     * Creates basic GET query for adding to BASE_URL to visit a specific product
     * listing page
     * @param pageNumber the page number to go to
     * @return the URL query for the chosen page number
     */
    public synchronized static String getUrlQuery(int pageNumber) {
        return String.format("?page=%d", pageNumber);
    }

    /**
     * Returns a copy of the Crawl Data
     * @return a copy of the Crawl data
     */
    public CrawlData getCrawlData() {
        return new CrawlData(crawlData.getTitle(), crawlData.getProductMap());
    }

    /**
     * Sets the crawl data to the given crawl data
     * @param crawlData the new crawl data to set for this SentaiFilmworksCrawler
     */
    public void setCrawlData(CrawlData crawlData) {
        this.crawlData = crawlData;
    }

    /**
     * Saves the HTML code of the INITIAL_URL contents to a local file
     * (BASE_PAGE_NAME)
     * @param getDataAgain true to retrieve the INITIAL_URL contents even if the base page is already detected,
     *                     false to not retrieve new contents when the base page is already detected
     */
    public void saveBasePage(boolean getDataAgain) {
        // If user does not want to re-obtain data in case of base page already existing, then
        //   check for existing base page file and return if it exists
        if(!getDataAgain) {
            File file = new File(BASE_PAGE_NAME);
            if(file.exists()) {
                return;
            }
        }

        String fullPageHTML = super.readInitialURLContents();
        BufferedWriter bufferedWriter;
        try {
            File file = new File(BASE_PAGE_PATH);
            if(!file.exists()) {
                file.mkdirs();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(BASE_PAGE_NAME));
            bufferedWriter.write(fullPageHTML);
            bufferedWriter.close();  // Close here instead of "finally" block, as it might throw IOException
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Saves the HTML code of the given productURL to a local file
     * @param productURL URL of product page to save locally
     * @param getDataAgain true to retrieve product information again if already detected,
     *                     false to not retrieve new product page contents if page is already detected
     */
    public void saveProductPage(String productURL, boolean getDataAgain) {
        // First determine name of product (to use in filename)
        String productName = productURL.substring(productURL.lastIndexOf('/') + 1);
        if(productName.contains("?")) {
            productName = productName.substring(0, productName.indexOf('?'));
        }

        // If user does not want to re-obtain data in case of base page already existing, then
        //   check for existing base page file and return if it exists
        if(!getDataAgain) {
            File file = new File(SAMPLE_PRODUCT_PAGES_PATH + productName + ".html");
            if(file.exists()) {
                return;
            }
        }

        String fullPageHTML = WebCrawler.readUrlContents(productURL);
        BufferedWriter bufferedWriter;
        try {
            File file = new File(SAMPLE_PRODUCT_PAGES_PATH);
            if(!file.exists()) {
                file.mkdirs();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(SAMPLE_PRODUCT_PAGES_PATH + productName + ".html"));
            bufferedWriter.write(fullPageHTML);
            bufferedWriter.close();  // Close here instead of "finally" block, as it might throw IOException
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Attempts to parse the base page (created with saveBasePage() method)
     */
    public void parseBasePage() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // Read in the file
            BufferedReader bufferedReader = new BufferedReader(new FileReader(BASE_PAGE_NAME));
            String currentLine = bufferedReader.readLine();
            while(currentLine != null) {
                stringBuilder.append(currentLine);
                stringBuilder.append("\n");
                currentLine = bufferedReader.readLine();
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.err.println("[ERROR] Could not find file " + BASE_PAGE_NAME + ", make sure it has been created!");
            ex.printStackTrace();
        }
        catch(IOException ex) {
            System.err.println("[ERROR] Could not close reader going through " + BASE_PAGE_NAME);
            ex.printStackTrace();
        }

        // Use Jsoup to start parsing the HTML code of the base page
        Document document = Jsoup.parse(stringBuilder.toString());

        // Find products portion of page
        Element allProductsElement = document.getElementById(PRODUCTS_ID);

        // Extract the JSON objects representing each product, and then parse the object to get Product information
        Elements productsForms = allProductsElement.getElementsByTag("form");

        List<JSONObject> JSONProducts = new ArrayList<JSONObject>();
        for (Element productForm : productsForms) {

            // Find the id of the product, to make request for product information JSON object
            Element formatSelector = productForm.select("div > ul > li > div > select").first();
            Element formatSelectorParent = formatSelector.parent();
            String productAJAXResource = STORE_URL + "/products/" + formatSelectorParent.id() + ".js";
            String productJsonString = readUrlContents(productAJAXResource);

            // Create JSONObject from the product JSON String and add it to the JSONProducts list
            JSONObject productJson = new JSONObject(productJsonString);

            // Go through the JSONProducts list to get information about all the products listed, wrapped in try-catch to know when something goes wrong in parsing
            try {
                JSONArray productVariants = productJson.getJSONArray(PRODUCT_JSON_VARIANTS_KEY);
                String productLinkComponent = productJson.getString(PRODUCT_JSON_URL_COMPONENT_KEY);
                String productLink = PRODUCT_BASE_URL + "/" + productLinkComponent;

                // Loop through the variants (there is one variant per video format product can be bought in)
                for(int i = 0; i < productVariants.length(); i++) {
                    System.out.println(productLink);

                    JSONObject productVariant = productVariants.getJSONObject(i);
                    String productFullName = productJson.getString(PRODUCT_TITLE_KEY);

                    // Need to add format to title ONLY if the product has multiple variants
                    if(productVariants.length() > 1) {
                        productFullName += " " + productVariant.getString(PRODUCT_VARIANT_KEY);
                    }

                    System.out.println(productFullName);

                    double productPrice = productVariant.getInt(PRODUCT_PRICE_KEY) / PRODUCT_PRICE_MULTIPLIER;
                    System.out.println("Price ($): " + productPrice);

                    System.out.println();
                }
            }
            catch(JSONException ex) {
                ex.printStackTrace();
                System.err.println("[ERROR] Could not parse product information from following JSON (skipping it): " + productJsonString);
            }
            catch(Exception ex) {
                ex.printStackTrace();
                System.err.println("[ERROR] Unknown error occurred");
            }
        }

        // Get link to next page
        Element paginationElement = document.getElementById(PAGINATION_ID);
        Elements paginationLinks = paginationElement.select("ul > li > a");
        String nextPageLink = STORE_URL + paginationLinks.last().attr("href");
        System.out.println("Next page: " + nextPageLink);
    }

    /**
     * Attempts to parse a saved product page that had initially been retrieved from the productURL
     * @param productURL the URL of the product to parse
     */
    public void parseProductPage(String productURL) {
        // First determine name of product (to use in filename)
        String productName = productURL.substring(productURL.lastIndexOf('/') + 1);
        if(productName.contains("?")) {
            productName = productName.substring(0, productName.indexOf('?'));
        }
        String productFilename = SAMPLE_PRODUCT_PAGES_PATH + productName + ".html";

        StringBuilder stringBuilder = new StringBuilder();
        try {
            // Read in the file
            BufferedReader bufferedReader = new BufferedReader(new FileReader(productFilename));
            String currentLine = bufferedReader.readLine();
            while(currentLine != null) {
                stringBuilder.append(currentLine);
                stringBuilder.append("\n");
                currentLine = bufferedReader.readLine();
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.err.println("[ERROR] Could not find file " + productFilename + ", make sure it has been created!");
            ex.printStackTrace();
        }
        catch(IOException ex) {
            System.err.println("[ERROR] Could not close reader going through " + productFilename);
            ex.printStackTrace();
        }

        // Use Jsoup to start parsing the HTML code of the base page
        Document document = Jsoup.parse(stringBuilder.toString());

        // Get the product description portion of the document
        Element productDescriptionElement = document.getElementById(PRODUCT_DESCRIPTION_ID);

        // Get the title of the product
        String productTitle = productDescriptionElement.select("h2").html();
        System.out.println(productTitle);

        Elements formatSelectors = productDescriptionElement.getElementsByClass(PRODUCT_FORMAT_SELECTOR_CLASS);
        if(formatSelectors.size() == 0) {  // Only 1 format available
            System.out.println("Single format detected");
            Element priceInformationElement = productDescriptionElement.getElementById(PRODUCT_PRICE_ID);
            System.out.println(priceInformationElement);
            String currentPrice = priceInformationElement.getElementsByClass(PRODUCT_CURRENT_PRICE_CLASS).first().html();
            System.out.println(currentPrice);
            String Msrp = priceInformationElement.getElementsByClass(PRODUCT_MSRP_CLASS).first().html();
            System.out.println(Msrp);
        }
        else {  // Multiple formats (Blu-Ray and DVD) available
            System.out.println("Multi format detected");
            Element formatSelectorsSelectTag = formatSelectors.first().getElementsByTag("select").last();
            Elements formatOptions = formatSelectorsSelectTag.getElementsByTag("option");
            for(Element formatOption : formatOptions) {
                System.out.println(formatOption);
                String variationID = formatOption.attr("value");
                System.out.println(variationID);
                String variationURL = productURL + PRODUCT_VARIATION_QUERY + variationID;
                System.out.println(variationURL);
                String formatType = formatOption.html();
                formatType = formatType.substring(0, formatType.lastIndexOf(" - "));
                System.out.println(formatType + " format pricing:");
                printPriceOfProductVariant(variationURL);
            }
        }
        System.out.println();
    }

    /**
     * Gets links for products
     * @param productURL URL that leads to product page (without any GET query)
     * @return Map of all the different possible product variant URLs, mapped to by product format
     */
    private Map<String, String> findProductLinks(String productURL) {
        String productHTML = WebCrawler.readUrlContents(productURL);
        Map<String, String> productLinks = new HashMap<>();

        // Use Jsoup to start parsing the HTML code of the base page
        Document document = Jsoup.parse(productHTML);

        // Get the product description portion of the document
        Element productDescriptionElement = document.getElementById(PRODUCT_DESCRIPTION_ID);

        Elements formatSelectors = productDescriptionElement.getElementsByClass(PRODUCT_FORMAT_SELECTOR_CLASS);

        if(formatSelectors.size() == 0) {  // Only 1 format available
            productLinks.put(SINGLE_FORMAT_PRODUCT, productURL);
            return productLinks;
        }

        // Multiple formats available, so parse the select element options to get the URLs to the different formats
        Element formatSelectorsSelectTag = formatSelectors.first().getElementsByTag("select").last();
        Elements formatOptions = formatSelectorsSelectTag.getElementsByTag("option");
        for(Element formatOption : formatOptions) {
            String variationID = formatOption.attr("value");
            String variationURL = productURL + PRODUCT_VARIATION_QUERY + variationID;
            String formatType = formatOption.html();
            formatType = formatType.substring(0, formatType.lastIndexOf(" - "));
            productLinks.put(formatType, variationURL);
        }
        return productLinks;
    }

    /**
     * Prints price (current and MSRP) of product at the given URL
     * @param variantURL the product page to pull price information from
     */
    private void printPriceOfProductVariant(String variantURL) {
        String productHTML = WebCrawler.readUrlContents(variantURL);

        // Use Jsoup to start parsing the HTML code of the base page
        Document document = Jsoup.parse(productHTML);

        // Get the product description portion of the document
        Element productDescriptionElement = document.getElementById(PRODUCT_DESCRIPTION_ID);

        // Get price information
        Element priceInformationElement = productDescriptionElement.getElementById(PRODUCT_PRICE_ID);
        System.out.println(priceInformationElement);
        String currentPrice = priceInformationElement.getElementsByClass(PRODUCT_CURRENT_PRICE_CLASS).first().html();
        System.out.println(currentPrice);
        String Msrp = priceInformationElement.getElementsByClass(PRODUCT_MSRP_CLASS).first().html();
        System.out.println(Msrp);
    }

    /**
     * Attempts to visit all store pages with product listings
     * @param printProgress true to print out crawling progress to standard output, false to not print
     * @return true if visiting all pages worked without issue, false if an error occurred during the process
     */
    public boolean visitAllPages(boolean printProgress) {
        return visitPage(INITIAL_URL, printProgress, true);
    }

    /**
     * Attempts to visit all store pages with product listings by utilizing multithreading to
     * visit multiple product listing pages at the same time
     * @param printProgress true to print out crawling progress to standard output, false to not print
     * @return true if visiting all pages worked without issue, false if an error occurred during the process
     */
    public boolean visitAllPagesMultithreaded(final boolean printProgress) {
        // Create list of page visitors and set up each one to give back results from the page they visit
        final int NUMBER_OF_PAGES_TO_VISIT = findNumberOfListingPages();
        List<CompletableFuture<Boolean>> pageVisitors = new ArrayList<>(NUMBER_OF_PAGES_TO_VISIT);
        for(int i = 1; i <= NUMBER_OF_PAGES_TO_VISIT; i++) {
            final int pageIndex = i;
            CompletableFuture<Boolean> pageVisitor = CompletableFuture.supplyAsync(() -> {
                String urlToVisit = BASE_URL + getUrlQuery(pageIndex);
                return visitPage(urlToVisit, printProgress, false);
            });
            pageVisitors.add(pageVisitor);
        }

        // Wait for all the page visitors to finish
        boolean foundAllProducts = false;
        for(int i = 0; i < pageVisitors.size(); i++) {
            CompletableFuture<Boolean> pageVisitor = pageVisitors.get(i);
            try {
                // Get result back from the page visitor
                foundAllProducts = pageVisitor.get();
            }
            catch(ExecutionException | InterruptedException ex) {  // Catch any potential errors
                ex.printStackTrace();
            }
        }
        return foundAllProducts;
    }

    /**
     * Uses some math on the INITIAL_URL page o determine how many pages of product listings should be
     * visited to visit all products (plus buffer of 1 extra page)
     * @return the number of expected pages for Sentai Filmworks product listings, plus 1
     */
    public static int findNumberOfListingPages() {
        // Visit the INITIAL_URL
        String pageHTML = WebCrawler.readUrlContents(INITIAL_URL);

        // Use Jsoup to start parsing the HTML code of the base page
        Document document = Jsoup.parse(pageHTML);

        // Find the element with the link to last page (and extract that page number)
        Element paginationElement = document.getElementById(PAGINATION_ID);
        Elements paginationLinks = paginationElement.select("ul > li > a");

        // The final page link comes before the last link (that links to next page), so keep second-to-last Element
        Element finalPageElement = paginationLinks.get(paginationLinks.size() - 2);

        int nextPageValue = Integer.parseInt(finalPageElement.html());
        return nextPageValue + 1;  // Do + 1 to buffer page visits by 1 more
    }

    /**
     * Visits the given pageURL and looks for link to next page to visit that link, updating the crawl
     * data with Product information as data is analyzed
     * @param pageURL URL to visit
     * @param printProgress true to print out crawling progress to standard output, false to not print
     * @param visitAllPages true to recursively visit all pages starting from given pageURL
     * @return true if visiting all pages was successful, false if there was an error during the process
     */
    private boolean visitPage(String pageURL, boolean printProgress, boolean visitAllPages) {
        String pageHTML = WebCrawler.readUrlContents(pageURL);
        if(pageHTML == null) {  // readUrlContents() failed for some reason or another, so return false
            System.err.println("Could not read URL contents of " + pageURL);
            return false;
        }

        // Use Jsoup to start parsing the HTML code of the page
        Document document = Jsoup.parse(pageHTML);

        // Find products portion of page
        Element allProductsElement = document.getElementById(PRODUCTS_ID);

        // Extract the JSON objects representing each product, and then parse the object to get Product information
        Elements productsForms = allProductsElement.getElementsByTag("form");

        List<JSONObject> JSONProducts = new ArrayList<JSONObject>();
        for (Element productForm : productsForms) {

            // Find the id of the product, to make request for product information JSON object
            Element formatSelector = productForm.select("div > ul > li > div > select").first();
            Element formatSelectorParent = formatSelector.parent();
            String productAJAXResource = STORE_URL + "/products/" + formatSelectorParent.id() + ".js";
            String productJsonString = readUrlContents(productAJAXResource);

            // Create JSONObject from the product JSON String and add it to the JSONProducts list
            JSONObject productJson = new JSONObject(productJsonString);

            // Go through the JSONProducts list to get information about all the products listed, wrapped in try-catch to know when something goes wrong in parsing
            try {
                JSONArray productVariants = productJson.getJSONArray(PRODUCT_JSON_VARIANTS_KEY);
                String productLinkComponent = productJson.getString(PRODUCT_JSON_URL_COMPONENT_KEY);
                String productLink = PRODUCT_BASE_URL + "/" + productLinkComponent;

                // Loop through the variants (there is one variant per video format product can be bought in)
                for(int i = 0; i < productVariants.length(); i++) {
                    JSONObject productVariant = productVariants.getJSONObject(i);
                    String productFullName = productJson.getString(PRODUCT_TITLE_KEY);

                    // Need to add format to title ONLY if the product has multiple variants
                    if(productVariants.length() > 1) {
                        productFullName += " " + productVariant.getString(PRODUCT_VARIANT_KEY);
                    }

                    double productPrice = productVariant.getInt(PRODUCT_PRICE_KEY) / PRODUCT_PRICE_MULTIPLIER;

                    // Now update crawl data with product information
                    updateCrawlData(productFullName, productLink, productPrice, printProgress);
                }
            }
            catch(JSONException ex) {
                ex.printStackTrace();
                System.err.println("[ERROR] Could not parse product information from following JSON (skipping it): " + productJsonString);
            }
            catch(Exception ex) {
                ex.printStackTrace();
                System.err.println("[ERROR] Unknown error occurred");
            }
        }

        // Get link to next page (if it exists)
        Element paginationElement = document.getElementById(PAGINATION_ID);
        Elements paginationLinks = paginationElement.select("ul > li > a");
        String nextPageLink = STORE_URL + paginationLinks.last().attr("href");
        String nextPageLinkContents = paginationLinks.last().html();
        if(NEXT_PAGE_HTML.equals(nextPageLinkContents)) {  // Make sure last link points to next page
            if(visitAllPages) {  // If we are supposed to visit all pages, call method recursively on next page
                return visitPage(nextPageLink, printProgress, true);
            }
            else {  // If we are not supposed to visit all pages, return false to indicate more pages do exist
                return false;
            }
        }
        // If last link does not point to next page, we must be on the last page now so we are done
        return true;
    }

    /**
     * Adds a new Product (or updates existing one with same name) in the crawl data
     * @param productName the name of the product
     * @param productURL the link to the product
     * @param price the current price of the product
     * @param printProduct true to print the product information to standard output, false to not print
     */
    private synchronized void updateCrawlData(String productName, String productURL, double price, boolean printProduct) {
        // Create a Product with a PriceDateInfo corresponding to info in given parameters
        Product productToAdd = new Product(productName, productURL);
        PriceDateInfo productPriceInfo = new PriceDateInfo(price);
        productToAdd.addNewPriceDateInfo(productPriceInfo);

        if(printProduct) {
            System.out.println(productToAdd);
        }

        // Add the product to the crawl data
        crawlData.addProduct(productToAdd);
    }

    /**
     * Returns the contents of the crawl data as a String with "Sentai Filmworks Crawler" on a preceding line
     * @return the contents of the crawl data as a String with "Sentai Filmworks Crawler" on a preceding line
     */
    @Override
    public String toString() {
        String result = "Sentai Filmworks Crawler\n";
        result += crawlData.toString();
        return result;
    }

}
