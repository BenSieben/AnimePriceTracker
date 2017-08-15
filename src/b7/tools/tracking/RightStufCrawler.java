package b7.tools.tracking;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

/**
 * WebCrawler that is specifically customized for the Anime
 * listings on Right Stuf's online store
 */
public class RightStufCrawler extends WebCrawler {

    // The base URL to start crawling from
    public final static String INITIAL_URL = "https://www.rightstufanime.com/category/Blu~ray,DVD?page=1&show=96";

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
     * Returns a copy of the Crawl Data
     * @return a copy of the Crawl data
     */
    public CrawlData getCrawlData() {
        System.out.println(crawlData);
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
        String fullPageHTML = WebCrawler.readUrlContentsWithJavaScript(INITIAL_URL, null);
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
        System.out.println(document.html());

        // Find elements which have matching product class, so that we can extract information from each one
        Elements productElements = document.getElementsByClass("N/A");
        for(Element product : productElements) {
            // Get the product info inside each product element
            // Extract the URL to the product page
            // Extract title (and convert any HTML entities like &amp; back to regular characters)
            // Extract product price(s) - may be 1 or 2 prices depending on formats offered for the product
        }

        // Get link to next page

    }
}
