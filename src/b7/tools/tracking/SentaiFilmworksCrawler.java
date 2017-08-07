package b7.tools.tracking;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

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
    public final static String PRODUCT_NAME_ATTR = "data-alpha";
    public final static String PRODUCT_PRICE_ATTR = "data-price";
    public final static String PRODUCT_INFO_ID = "product-info";
    public final static String PRODUCT_TITLE_CLASS = "prod-title";
    public final static String PRODUCT_PRICE_CLASS = "prod-class";

    // Path we will save the test base page in (so we can create directory if it doesn't already exist)
    public final static String BASE_PAGE_PATH = "savedata/basepages/";

    // Where we will save the test base page
    public final static String BASE_PAGE_NAME = BASE_PAGE_PATH + "sentaifilmworks.html";

    /**
     * Creates a new SentaiFilmworksCrawler
     */
    public SentaiFilmworksCrawler() {
        super(INITIAL_URL);
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

        String fullPageHTML = super.getInitialURLContents();
        BufferedWriter bufferedWriter = null;
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
        try {
            // Read in the file
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(BASE_PAGE_NAME));
            String currentLine = bufferedReader.readLine();
            while(currentLine != null) {
                stringBuilder.append(currentLine);
                stringBuilder.append("\n");
                currentLine = bufferedReader.readLine();
            }
            bufferedReader.close();

            // Use Jsoup to start parsing the HTML code of the base page
            Document document = Jsoup.parse(stringBuilder.toString());

            // TODO parse the listings on the current page
            /*
            <ul id="product-loop">
                <li class="product col-xs-12 col-sm-6 col-md-3 col-lg-3 home-featured-products  first" data-alpha="11 Eyes Complete Collection" data-price="3499">
                    <div containing more details like link to full page for listed item, etc.>
                </li>
            </ul>
             */
            // Find elements which have matching product class, so that we can extract information from each one
            Elements productElements = document.getElementsByClass(PRODUCT_CLASS);
            for(Element product : productElements) {
                System.out.println(product.attr(PRODUCT_NAME_ATTR));
                System.out.println(product.attr(PRODUCT_PRICE_ATTR));
                Element productInfo = product.getElementById(PRODUCT_INFO_ID);
                System.out.println(productInfo);
                Elements productLinks = productInfo.getElementsByTag("a");
                String productLink = STORE_URL + productLinks.first().attr("href");
                System.out.println(productLink);
                Element productTitleElement = productInfo.getElementsByClass(PRODUCT_TITLE_CLASS).first();
                // Extract title (and convert any HTML entities like &amp; back to regular characters)
                String productTitle = productTitleElement.getElementsByTag("p").first().html();
                productTitle = Jsoup.parse(productTitle).text();
                System.out.println(productTitle);
                System.out.println();
            }

            // TODO parse the location of the next page
            /*<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 pagination-container">
                 <hr>
                 <div id="pagination" class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                     <ul class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                         <li class="current-page" style="text-decoration: underline; font-weight: bold;"><a title="">1</a></li>
                         <li><a href="/collections/shows?page=2" title="">2</a></li>
                         <li><a href="/collections/shows?page=3" title="">3</a></li>
                         <li>…</li>
                         <li><a href="/collections/shows?page=34" title="">34</a></li>
                         <li><a href="/collections/shows?page=2" title="">&gt;</a></li>
                         <li> </li>
                     </ul>
                 </div>
                 <div id="count-top">
                     <span class="count">Items 1-16 of 534</span>
                 </div>
             </div>
             */

            //System.out.println(document);
        }
        catch(FileNotFoundException ex) {
            System.err.println("[ERROR] Could not find file " + BASE_PAGE_NAME + ", make sure it has been created!");
            ex.printStackTrace();
        }
        catch(IOException ex) {
            System.err.println("[ERROR] Could not close reader going through " + BASE_PAGE_NAME);
            ex.printStackTrace();
        }
    }

}
