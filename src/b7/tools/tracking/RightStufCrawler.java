package b7.tools.tracking;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * WebCrawler that is specifically customized for the Anime
 * listings on Right Stuf's online store
 */
public class RightStufCrawler extends WebCrawler {

    // The base URL to crawl from, not including query parameters
    public final static String BASE_URL = "https://www.rightstufanime.com/category/Blu~ray,DVD";

    // How many products we can view per page
    public static final int PRODUCTS_PER_LISTING_PAGE = 96;

    // The base URL to start crawling from
    public final static String INITIAL_URL = BASE_URL + getUrlQuery(1, PRODUCTS_PER_LISTING_PAGE);

    // The base URL of the website (to resolve relative links to the proper path)
    public final static String STORE_URL = "https://www.rightstufanime.com";

    // How long (in milliseconds) we will wait for the JavaScript on Right Stuf to fully load the web page
    public final static int PAGE_LOAD_WAIT_TIME = 5000;

    // Qualifiers to help use parse pages to get relevant content
    public static final String PRODUCT_INFORMATION_DIV_CLASS = "facets-item-cell-grid";
    public static final String PRODUCT_TITLE_CLASS = "facets-item-cell-grid-title";
    public static final String PRODUCT_PRICE_DIV_CLASS = "product-views-price";
    public static final String PRODUCT_PRICE_SPAN_CLASS = "product-views-price-lead";
    public static final String PRODUCT_PRICE_ATTRIBUTE = "data-rate";
    public static final String NEXT_PAGE_NAV_CLASS = "global-views-pagination";
    public static final String NEXT_PAGE_LIST_ITEM_CLASS = "global-views-pagination-next";
    public static final String NUMBER_OF_PRODUCTS_CLASS = "facets-facet-browse-title";
    public static final String NUMBER_OF_PRODUCTS_ATTRIBUTE = "data-quantity";

    // Path we will save the test base page in (so we can create directory if it doesn't already exist)
    public final static String BASE_PAGE_PATH = "savedata/basepages/";
    // Where we will save the test base page
    public final static String BASE_PAGE_NAME = BASE_PAGE_PATH + "rightstuf.html";

    public final static String CRAWL_DATA_TITLE = "Right Stuf Crawl Data";
    private CrawlData crawlData;  // The current CrawlData (which we can update with this crawler)

    /**
     * Constructs a new RightStufCrawler with empty crawl data
     */
    public RightStufCrawler() {
        super(INITIAL_URL);
        crawlData = new CrawlData(CRAWL_DATA_TITLE);
    }

    /**
     * Creates a new RightStufCrawler with the given initial crawl data
     * @param initialCrawlData the initial CrawlData to use
     */
    public RightStufCrawler(CrawlData initialCrawlData) {
        super(INITIAL_URL);
        crawlData = initialCrawlData;
    }

    /**
     * Creates basic GET query for adding to BASE_URL to visit a specific product
     * listing page with a specified number of listings to show on the page
     * @param pageNumber the page number to go to
     * @param productsToShowOnPage the number of products to show per page
     * @return the URL query for the chosen page number and products to show per page
     */
    public synchronized static String getUrlQuery(int pageNumber, int productsToShowOnPage) {
        return String.format("?page=%d&show=%d", pageNumber, productsToShowOnPage);
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

        // Use readUrlContentsWithJavaScript() too have a headless browser visit Right Stuf,
        //   because Right Stuf requires JavaScript to load their web page HTML properly
        String fullPageHTML = WebCrawler.readUrlContentsWithJavaScriptHtmlunit(INITIAL_URL);

        // We got back correct HTML from Right Stuf, so write the results to a file
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

        // Find elements which have matching product class, so that we can extract information from each one
        Elements productElements = document.getElementsByClass(PRODUCT_INFORMATION_DIV_CLASS);

        // Find elements which have matching product class, so that we can extract information from each one
        for(Element productElement : productElements) {
            // Extract title (and convert any HTML entities like &amp; back to regular characters)
            Element productTitleElement = productElement.getElementsByClass(PRODUCT_TITLE_CLASS).first();
            // The title text is surrounded by an anchor that links to the product page
            String relativeLink = productTitleElement.getElementsByTag("a").first().attr("href");
            String productLink = STORE_URL + relativeLink;
            System.out.println(productLink);
            // Pull title information and parse it to convert any special characters like "&amp;" back to "&"
            String productTitle = Jsoup.parse(productTitleElement.html()).text().trim();
            System.out.println(productTitle);

            // Extract product price
            // Get the div element which contains the product price information
            Element priceDivElement = productElement.getElementsByClass(PRODUCT_PRICE_DIV_CLASS).first();
            // Get the span element which specifically has the sale price (not the MSRP value)
            Element productPriceSpanElement = priceDivElement.getElementsByClass(PRODUCT_PRICE_SPAN_CLASS).first();;
            double productPrice = Double.parseDouble(productPriceSpanElement.attr(PRODUCT_PRICE_ATTRIBUTE));
            System.out.println("Price ($): " + productPrice);

            System.out.println();
        }

        // Find link to next page
        // Get nav element that has link to next page
        Element paginationNav = document.getElementsByClass(NEXT_PAGE_NAV_CLASS).last();
        //  Find next page list item from the nav element (should only be 1 result if next page element is present)
        Elements nextPageListItemElements = paginationNav.getElementsByClass(NEXT_PAGE_LIST_ITEM_CLASS);
        if(nextPageListItemElements.size() > 0) {   // Make sure the next page list item was present in the nav
            // Get direct access to next page list item element
            Element nextPageListItem = nextPageListItemElements.first();
            // Extract anchor href value from element link
            String nextPageAnchor = nextPageListItem.getElementsByTag("a").first().attr("href");
            String nextPageLink = STORE_URL + "/" + nextPageAnchor;
            System.out.println("Next page: " + nextPageLink);
        }
        else {
            // No next page list item found, so we are on the last page
        }
    }

    /**
     * Attempts to visit all store pages with product listings
     * @param printProgress true to print out crawling progress to standard output, false to not print
     * @return true if visiting all pages worked without issue, false if an error occurred during the process
     */
    public boolean visitAllPages(final boolean printProgress) {
        // Give message indicating that this process will take a while if printProgress is true
        int maxExpectedRuntime = 90;
        if(printProgress) {
            System.out.println("The update process can take up to an estimated " + maxExpectedRuntime +
                    " minutes due to how Right Stuf's website is designed. Please be patient\n");
        }

        return visitPage(INITIAL_URL, printProgress, true);
    }

    /**
     * Attempts to visit all store pages with product listings by utilizing multithreading to
     * visit multiple product listing pages at the same time
     * @param printProgress true to print out crawling progress to standard output, false to not print
     * @return true if visiting all pages worked without issue, false if an error occurred during the process
     */
    public boolean visitAllPagesMultithreaded(final boolean printProgress) {
        // Give message indicating that this process will take a while if printProgress is true
        int maxExpectedRuntime = 30;
        if(printProgress) {
            System.out.println("The update process can take up to an estimated " + maxExpectedRuntime +
                    " minutes due to how Right Stuf's website is designed. Please be patient\n");
        }

        // Create list of page visitors and set up each one to give back results from the page they visit
        final int NUMBER_OF_PAGES_TO_VISIT = findNumberOfListingPages();
        List<CompletableFuture<Boolean>> pageVisitors = new ArrayList<>(NUMBER_OF_PAGES_TO_VISIT);
        for(int i = 1; i <= NUMBER_OF_PAGES_TO_VISIT; i++) {
            final int pageIndex = i;
            CompletableFuture<Boolean> pageVisitor = CompletableFuture.supplyAsync(() -> {
                String urlToVisit = BASE_URL + getUrlQuery(pageIndex, PRODUCTS_PER_LISTING_PAGE);
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
     * Uses some math on the INITIAL_URL page to find out how many products are currently
     * on the Right Stuf store, to determine how many pages of product listings should be
     * visited to visit all products (plus buffer of 1 extra page)
     * @return the number of expected pages for Right Stuf product listings, plus 1
     */
    public static int findNumberOfListingPages() {
        // Visit the INITIAL_URL
        String pageHTML = WebCrawler.readUrlContentsWithJavaScriptHtmlunit(INITIAL_URL);

        // Use Jsoup to start parsing the HTML code of the base page
        Document document = Jsoup.parse(pageHTML);

        // Find the element that indicates how many total products are listed at the moment
        Element totalProductCountElement = document.getElementsByClass(NUMBER_OF_PRODUCTS_CLASS).first();

        // Get attribute information from the element to determine how many products there are
        int totalPages = Integer.parseInt(totalProductCountElement.attr(NUMBER_OF_PRODUCTS_ATTRIBUTE));

        // Divide total products by number of products per page to get final result of expected pages to visit
        return totalPages / PRODUCTS_PER_LISTING_PAGE + 2;  // + 2 because we lose 1 page to truncation after division
    }

    /**
     * Visits the given pageURL and looks for link to next page to visit that link, updating the crawl
     * data with Product information as data is analyzed
     * @param pageURL URL to visit
     * @param printProgress true to print out crawling progress to standard output, false to not print
     * @param visitAllPages true to recursively visit all pages starting from the given page, false to visit the given pageURL only
     * @return true if there is no more pages to visit, false if there is a link to a next page from the last visited page
     */
    private boolean visitPage(String pageURL, boolean printProgress, boolean visitAllPages) {
        // Use readUrlContentsWithJavaScript to load Right Stuf pages (since JavaScript is needed to view content)
        String pageHTML = WebCrawler.readUrlContentsWithJavaScriptHtmlunit(pageURL);

        // Use Jsoup to start parsing the HTML code of the base page
        Document document = Jsoup.parse(pageHTML);

        // Find elements which have matching product class, so that we can extract information from each one
        Elements productElements = document.getElementsByClass(PRODUCT_INFORMATION_DIV_CLASS);

        // Find elements which have matching product class, so that we can extract information from each one
        for(Element productElement : productElements) {
            // Extract title (and convert any HTML entities like &amp; back to regular characters)
            Element productTitleElement = productElement.getElementsByClass(PRODUCT_TITLE_CLASS).first();
            // The title text is surrounded by an anchor that links to the product page
            String relativeLink = productTitleElement.getElementsByTag("a").first().attr("href");
            String productLink = STORE_URL + relativeLink;
            // Pull title information and parse it to convert any special characters like "&amp;" back to "&"
            String productTitle = Jsoup.parse(productTitleElement.html()).text().trim();

            // Extract product price
            // Get the div element which contains the product price information
            Element priceDivElement = productElement.getElementsByClass(PRODUCT_PRICE_DIV_CLASS).first();
            // Get the span element which specifically has the sale price (not the MSRP value)
            Element productPriceSpanElement = priceDivElement.getElementsByClass(PRODUCT_PRICE_SPAN_CLASS).first();
            // Extract product price, and check if the extraction actually got a number or not (if not, retry loading the page again, as that is the likely cause of error)
            double productPrice;
            try {
                productPrice = Double.parseDouble(productPriceSpanElement.attr(PRODUCT_PRICE_ATTRIBUTE));

                // Add product information to the crawl data if we got the price successfully
                updateCrawlData(productTitle, productLink, productPrice, printProgress);
            }
            catch(NumberFormatException ex) {
                ex.printStackTrace();
                System.err.println("Could not get product Price for " + productTitle + " at " + productLink + " on page " + pageURL + " ; attempting to reload the page");
                // Try the page again if we ran into an issue parsing a product's price
                return visitPage(pageURL, printProgress, visitAllPages);
            }
        }

        // Find link to next page
        // Get nav element that has link to next page
        Element paginationNav = document.getElementsByClass(NEXT_PAGE_NAV_CLASS).last();

        //Make sure paginationNav exists (if it doesn't, we have hit a page that comes after final page)
        if(paginationNav == null) {
            return true;  // Indicate there are no more pages from this page
        }

        //  Find next page list item from the nav element (should only be 1 result if next page element is present)
        Elements nextPageListItemElements = paginationNav.getElementsByClass(NEXT_PAGE_LIST_ITEM_CLASS);

        if(nextPageListItemElements.size() > 0) {   // Make sure the next page list item was present in the nav
            // Get direct access to next page list item element
            Element nextPageListItem = nextPageListItemElements.first();
            // Extract anchor href value from element link
            String nextPageAnchor = nextPageListItem.getElementsByTag("a").first().attr("href");
            String nextPageLink = STORE_URL + "/" + nextPageAnchor;

            // Visit the next page
            if(visitAllPages) {  // If visitAllPages is true, then visit the next page
                return visitPage(nextPageLink, printProgress, true);
            }
            else {  // If visitAllPages is false, return false to indicate the next page or product listings exists for single-page call to visitPage()
                return false;
            }
        }

        // We reached the end of all pages, so return true (there are no more pages)
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
     * Returns the contents of the crawl data as a String with "Right Stuf Crawler" on a preceding line
     * @return the contents of the crawl data as a String with "Right Stuf Crawler" on a preceding line
     */
    @Override
    public String toString() {
        String result = "Right Stuf Crawler\n";
        result += crawlData.toString();
        return result;
    }
}
