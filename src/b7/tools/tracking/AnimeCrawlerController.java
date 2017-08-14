package b7.tools.tracking;

/**
 * Main controller of crawlers used for
 * tracking anime products
 */
public class AnimeCrawlerController {

    // Constants for where we will place save data for any crawlers we use
    public static final String CRAWLERS_PATH = "savedata/crawlers/";  // Folder we save crawlers in
    public static final String SENTAI_FILMWORKS_CRAWLER_FILENAME = CRAWLERS_PATH + "sentaifilmworks.json";
    public static final String CSVS_PATH = "savedata/csvs/";  // Folder we save CSVs from crawl data in
    public static final String SENTAI_FILMWORKS_CRAWLER_CSV_FILENAME = CSVS_PATH + "sentaifilmworks.csv";

    // The SentaiFilmworksCrawler being used by the controller
    private SentaiFilmworksCrawler sentaiFilmworksCrawler;

    /**
     * Constructs a new AnimeCrawlerController with no pre-loaded data
     */
    public AnimeCrawlerController() {
        sentaiFilmworksCrawler = new SentaiFilmworksCrawler();
    }

    /**
     * Constructs a new AnimeCrawler controller with pre-loaded data
     * @param sentaiFilmworksDataFilename filename for a previously-saved SentaiFilmworksCrawler object
     */
    public AnimeCrawlerController(String sentaiFilmworksDataFilename) {
        sentaiFilmworksCrawler = loadSentaiFilmworksCrawler(sentaiFilmworksDataFilename);
        if(sentaiFilmworksCrawler == null) {
            sentaiFilmworksCrawler = new SentaiFilmworksCrawler();
        }
    }

    /**
     * Uses the Sentai Filmworks Crawler to visit all pages (to get updated pricing information)
     * @param printProgress true to print out found products to standard output, false to not print
     */
    public void visitAllSentaiFilmworksPages(boolean printProgress) {
        sentaiFilmworksCrawler.visitAllPages(printProgress);
    }

    /**
     * Saves the SentaiFilmworksCrawler in this controller to the specified filename
     * @param filename the filename to use for the saved SentaiFilmworksCrawler
     */
    public void saveSentaiFilmworksCrawler(String filename) {
        CrawlerDataHandler.saveSentaiFilmworksCrawler(sentaiFilmworksCrawler, filename);
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
     * Saves the Crawl Data in the SentaiFilmworksCrawler in this controller in CSV format to the specified filename
     * that has appears correctly in Excel
     * @param filename the filename to use for the saved Crawl Data in CSV format from the SentaiFilmworksCrawler
     */
    public void saveSentaiFilmworksCrawlDataToExcelCSV(String filename) {
        CrawlerDataHandler.saveCrawlDataToExcelCSV(sentaiFilmworksCrawler.getCrawlData(), SENTAI_FILMWORKS_CRAWLER_CSV_FILENAME);
    }
}
