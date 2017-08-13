package b7.tools.tracking;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * WebCrawler that is specifically customized for the Anime
 * listings on Sentai Filmwork's online store
 */
public class SentaiFilmworksCrawler extends WebCrawler {

    // The base URL to start crawling from
    public final static String INITIAL_URL = "https://shop.sentaifilmworks.com/collections/shows?page=1";

    // The base URL of the website (to resolve relative links to the proper path)
    public final static String STORE_URL = "https://shop.sentaifilmworks.com";

    // Certain qualifiers used to help us search through pages for relevant information to extract
    public final static String PRODUCT_CLASS = "home-featured-products";
    public final static String PRODUCT_INFO_ID = "product-info";
    public final static String PRODUCT_TITLE_CLASS = "prod-title";
    public final static String PRODUCT_PRICE_CLASS = "prod-price";
    public final static String PAGINATION_ID = "pagination";
    public final static String NEXT_PAGE_HTML = "&gt;";

    // Certain qualifiers used to help us  search through product pages for relevant information
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

        // Find elements which have matching product class, so that we can extract information from each one
        Elements productElements = document.getElementsByClass(PRODUCT_CLASS);
        for(Element product : productElements) {
            // Get the product info inside each product element
            Element productInfo = product.getElementById(PRODUCT_INFO_ID);
            //System.out.println(productInfo);

            // Extract the URL to the product page
            Elements productLinkElements = productInfo.getElementsByTag("a");
            String productLink = STORE_URL + productLinkElements.first().attr("href");
            System.out.println(productLink);

            // Extract title (and convert any HTML entities like &amp; back to regular characters)
            Element productTitleElement = productInfo.getElementsByClass(PRODUCT_TITLE_CLASS).first();
            String productTitle = productTitleElement.getElementsByTag("p").first().html();
            productTitle = Jsoup.parse(productTitle).text();
            System.out.println(productTitle);

            // Extract product price(s) - may be 1 or 2 prices depending on formats offered for the product
            Elements productPriceElements = productInfo.getElementsByClass(PRODUCT_PRICE_CLASS);
            if(productPriceElements.size() == 1) {  // Only single format
                String singlePriceElement = productPriceElements.first().html();
                singlePriceElement = Jsoup.parse(singlePriceElement).text();
                double singlePrice = Double.parseDouble(
                        singlePriceElement.substring(singlePriceElement.indexOf("$") + 1));
                System.out.println("Price ($): " + singlePrice);
            }
            else {  // Has multiple formats (i.e., DVD and Blu-Ray)
                Map<String, String> productLinks = findProductLinks(productLink);
                for(Element priceElement : productPriceElements) {
                    String productPrice = priceElement.getElementsByTag("p").first().html();
                    productPrice = Jsoup.parse(productPrice).text();
                    String[] splitProductPrice = productPrice.split(": \\$ ");
                    String formatType = splitProductPrice[0];
                    double formatPrice = Double.parseDouble(splitProductPrice[1]);
                    System.out.println("Format: " + formatType +
                            ", Price ($): " + formatPrice + ", Link: " + productLinks.get(formatType));
                }
            }
            System.out.println();
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
     */
    public void visitAllPages() {
        visitAllPages(INITIAL_URL);
    }

    /**
     * Visits the given pageURL and looks for link to next page to visit that link
     * @param pageURL URL to visit
     */
    private void visitAllPages(String pageURL) {
        String pageHTML = WebCrawler.readUrlContents(pageURL);

        // Use Jsoup to start parsing the HTML code of the page
        Document document = Jsoup.parse(pageHTML);

        // Find elements which have matching product class, so that we can extract information from each one
        Elements productElements = document.getElementsByClass(PRODUCT_CLASS);
        for(Element product : productElements) {
            // Get the product info inside each product element
            Element productInfo = product.getElementById(PRODUCT_INFO_ID);
            //System.out.println(productInfo);

            // Extract the URL to the product page
            Elements productLinks = productInfo.getElementsByTag("a");
            String productLink = STORE_URL + productLinks.first().attr("href");
            //System.out.println(productLink);

            // Extract title (and convert any HTML entities like &amp; back to regular characters)
            Element productTitleElement = productInfo.getElementsByClass(PRODUCT_TITLE_CLASS).first();
            String productTitle = productTitleElement.getElementsByTag("p").first().html();
            productTitle = Jsoup.parse(productTitle).text();
            //System.out.println(productTitle);

            // Extract product price(s) - may be 1 or 2 prices depending on formats offered for the product
            Elements productPriceElements = productInfo.getElementsByClass(PRODUCT_PRICE_CLASS);
            if(productPriceElements.size() == 1) {  // Only single format
                //System.out.println("Single format detected");
                String singlePriceElement = productPriceElements.first().html();
                singlePriceElement = Jsoup.parse(singlePriceElement).text();
                //System.out.println(singlePriceElement);
            }
            else {  // Has multiple formats (i.e., DVD and Blu-Ray)
                //System.out.println("Multi format detected");
                for(Element priceElement : productPriceElements) {
                    String productPrice = priceElement.getElementsByTag("p").first().html();
                    productPrice = Jsoup.parse(productPrice).text();
                    //System.out.println(productPrice);
                }
            }
            //System.out.println();
        }

        // Get link to next page (if it exists)
        Element paginationElement = document.getElementById(PAGINATION_ID);
        //System.out.println(paginationElement);
        Elements paginationLinks = paginationElement.select("ul > li > a");
        String nextPageLink = STORE_URL + paginationLinks.last().attr("href");
        String nextPageLinkContents = paginationLinks.last().html();
        if(NEXT_PAGE_HTML.equals(nextPageLinkContents)) {  // Make sure last link points to next page
            System.out.println("Found next page: " + nextPageLink);
            visitAllPages(nextPageLink);
        }
        else {
            // If last link does not point to next page, we must be on the last page now
            System.out.println("Last page met (last link in pagination is " + nextPageLinkContents + ")");
        }
    }

}
