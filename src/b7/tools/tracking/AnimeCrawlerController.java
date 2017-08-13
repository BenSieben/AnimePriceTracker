package b7.tools.tracking;

/**
 * Main controller of crawlers used for
 * tracking anime products
 */
public class AnimeCrawlerController {

    // Constants for where we will place save data for any crawlers we use
    public static final String CRAWLERS_PATH = "savedata/crawlers/";  // Folder we save crawlers in
    public static final String SENTAI_FILMWORKS_CRAWLER_FILENAME = CRAWLERS_PATH + "sentaifilmworks.json";

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
    }

    /**
     * Uses the Sentai Filmworks Crawler to visit all pages (to get updated pricing information)
     */
    public void visitAllSentaiFilmworksPages() {
        sentaiFilmworksCrawler.visitAllPages();
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
}
