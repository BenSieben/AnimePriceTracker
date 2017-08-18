package b7.tools.tracking;

/**
 * Main controller of crawlers used for
 * tracking anime products
 */
public class AnimeCrawlerController {

    // Constants for where we will place save data for any crawlers we use
    public static final String CRAWLERS_PATH = "savedata/crawlers/";  // Folder we save crawlers in
    public static final String SENTAI_FILMWORKS_CRAWLER_FILENAME = CRAWLERS_PATH + "sentaifilmworks.json";
    public static final String RIGHT_STUF_CRAWLER_FILENAME = CRAWLERS_PATH + "rightstuf.json";
    public static final String CSVS_PATH = "savedata/csvs/";  // Folder we save CSVs from crawl data in
    public static final String SENTAI_FILMWORKS_CRAWLER_CSV_FILENAME = CSVS_PATH + PriceDateInfo.findCurrentDateString() + "_sentaifilmworks.csv";
    public static final String RIGHT_STUF_CRAWLER_CSV_FILENAME = CSVS_PATH + PriceDateInfo.findCurrentDateString() + "_rightstuf.csv";

    // The SentaiFilmworksCrawler being used by the controller
    private SentaiFilmworksCrawler sentaiFilmworksCrawler;

    // The RightStufCrawler being used by the controller
    private RightStufCrawler rightStufCrawler;

    /**
     * Constructs a new AnimeCrawlerController with no pre-loaded data
     */
    public AnimeCrawlerController() {
        sentaiFilmworksCrawler = new SentaiFilmworksCrawler();
        rightStufCrawler = new RightStufCrawler();
    }

    /**
     * Constructs a new AnimeCrawler controller with pre-loaded data
     * @param sentaiFilmworksDataFilename filename for a previously-saved SentaiFilmworksCrawler object
     * @param rightStufDataFilename filename for a previously-saved RightStufCrawler object
     */
    public AnimeCrawlerController(String sentaiFilmworksDataFilename, String rightStufDataFilename) {
        sentaiFilmworksCrawler = loadSentaiFilmworksCrawler(sentaiFilmworksDataFilename);
        if(sentaiFilmworksCrawler == null) {
            sentaiFilmworksCrawler = new SentaiFilmworksCrawler();
        }
        rightStufCrawler = loadRightStufCrawler(rightStufDataFilename);
        if(rightStufCrawler == null) {
            rightStufCrawler = new RightStufCrawler();
        }
    }

    /**
     * Uses the Sentai Filmworks Crawler to visit all pages (to get updated pricing information)
     * @param printProgress true to print out found products to standard output, false to not print
     * @return true if all pages were successfully visited, false otherwise
     */
    public boolean visitAllSentaiFilmworksPages(boolean printProgress) {
        return sentaiFilmworksCrawler.visitAllPages(printProgress);
    }

    /**
     * Uses the Right Stuf Crawler to visit all pages (to get updated pricing information)
     * @param printProgress true to print out found products to standard output, false to not print
     * @return true if all pages were successfully visited, false otherwise
     */
    public boolean visitAllRightStufPages(boolean printProgress) {
        return rightStufCrawler.visitAllPages(printProgress);
    }

    /**
     * Saves the SentaiFilmworksCrawler in this controller to the specified filename
     * @param filename the filename to use for the saved SentaiFilmworksCrawler
     */
    public void saveSentaiFilmworksCrawler(String filename) {
        CrawlerDataHandler.saveSentaiFilmworksCrawler(sentaiFilmworksCrawler, filename);
    }

    /**
     * Saves the RightStufCrawler in this controller to the specified filename
     * @param filename the filename to use for the saved RightStufCrawler
     */
    public void saveRightStufCrawler(String filename) {
        CrawlerDataHandler.saveRightStufCrawler(rightStufCrawler, filename);
    }

    /**
     * Loads a SentaiFilmworksCrawler from the filename
     * @param filename the file to load data from
     * @return SentaiFilmworksCrawler on successful load, null on failed load
     */
    private SentaiFilmworksCrawler loadSentaiFilmworksCrawler(String filename) {
        return CrawlerDataHandler.loadSentaiFilmworksCrawler(filename);
    }

    /**
     * Loads a RightStufCrawler from the filename
     * @param filename the file to load data from
     * @return RightStufCrawler on successful load, null on failed load
     */
    private RightStufCrawler loadRightStufCrawler(String filename) {
        return CrawlerDataHandler.loadRightStufCrawler(filename);
    }

    /**
     * Saves the Crawl Data in the SentaiFilmworksCrawler in this controller in CSV format to the specified filename
     * that has appears correctly in Excel
     * @param filename the filename to use for the saved Crawl Data in CSV format from the SentaiFilmworksCrawler
     */
    public void saveSentaiFilmworksCrawlDataToExcelCSV(String filename) {
        CrawlerDataHandler.saveCrawlDataToExcelCSV(sentaiFilmworksCrawler.getCrawlData(), filename);
    }

    /**
     * Saves the Crawl Data in the RightStufCrawler in this controller in CSV format to the specified filename
     * that has appears correctly in Excel
     * @param filename the filename to use for the saved Crawl Data in CSV format from the RightStufCrawler
     */
    public void saveRightStufCrawlDataToExcelCSV(String filename) {
        CrawlerDataHandler.saveCrawlDataToExcelCSV(rightStufCrawler.getCrawlData(), filename);
    }

    /**
     * Opens a new AnimePriceTrackerGUI, and waits for the GUI to
     * be closed before exiting the method
     */
    public void openGUI() {
        AnimePriceTrackerGUI animePriceTrackerGUI = new AnimePriceTrackerGUI();

        // Use a repeatedly-checking while loop on whether or not the GUI has been closed yet
        final int sleepTimeMillis = 1000;  // How long (milliseconds) to wait between checks on GUI closing state
        while(!animePriceTrackerGUI.hasClosed()) {
            try {
                Thread.sleep(sleepTimeMillis);
            }
            catch(InterruptedException ex) {
                System.err.println("[ERROR] While waiting for GUI to close, interruption exception occurred");
                ex.printStackTrace();
            }
        }
    }
}
