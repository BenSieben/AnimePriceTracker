import b7.tools.tracking.AnimeCrawlerController;
import b7.tools.tracking.SentaiFilmworksCrawler;

/**
 * Class with main method to run the program to get price information
 * for anime from online stores
 */
public class AnimePriceTracker {

    public static void main(String[] args) {
        //parseBaseSentaiFilmworksPage();
        //visitAllSentaiFilmworksPages();
        //parseSampleSentaiFilmworksProductPages();
        runAnimeCrawlerController();
    }

    /**
     * Sets up code to call methods to parse the base Sentai Filmworks store page
     */
    private static void parseBaseSentaiFilmworksPage() {
        SentaiFilmworksCrawler sentaiFilmworksCrawler = new SentaiFilmworksCrawler();
        sentaiFilmworksCrawler.saveBasePage(false);
        sentaiFilmworksCrawler.parseBasePage();
    }

    /**
     * Sets up code to call methods to traverse all pages for Sentai Filmworks store
     */
    private static void visitAllSentaiFilmworksPages() {
        SentaiFilmworksCrawler sentaiFilmworksCrawler = new SentaiFilmworksCrawler();
        sentaiFilmworksCrawler.visitAllPages();
    }

    /**
     * Sets up code to call methods to save and parse some sample product pages
     */
    private static void parseSampleSentaiFilmworksProductPages() {
        SentaiFilmworksCrawler sentaiFilmworksCrawler = new SentaiFilmworksCrawler();

        // Pick some sample product pages
        String productURL1 = "https://shop.sentaifilmworks.com/products/young-black-jack-complete-collection";
        String productURL2 = "https://shop.sentaifilmworks.com/products/yumeria-complete-collection";
        String productURL3 = "https://shop.sentaifilmworks.com/products/yuyushiki-complete-collection";

        // Save those product pages (do not re-get them if they already exist)
        sentaiFilmworksCrawler.saveProductPage(productURL1, false);
        sentaiFilmworksCrawler.saveProductPage(productURL2, false);
        sentaiFilmworksCrawler.saveProductPage(productURL3, false);

        // Parse those product pages
        sentaiFilmworksCrawler.parseProductPage(productURL1);
        sentaiFilmworksCrawler.parseProductPage(productURL2);
        sentaiFilmworksCrawler.parseProductPage(productURL3);
    }

    /**
     * Runs the AnimeCrawlerController
     */
    private static void runAnimeCrawlerController() {
        long startTime = System.currentTimeMillis();
        AnimeCrawlerController animeCrawlerController = new AnimeCrawlerController(AnimeCrawlerController.SENTAI_FILMWORKS_CRAWLER_FILENAME);
        animeCrawlerController.visitAllSentaiFilmworksPages();
        animeCrawlerController.saveSentaiFilmworksCrawler(AnimeCrawlerController.SENTAI_FILMWORKS_CRAWLER_FILENAME);
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        double runTimeInSeconds = runTime / 1000.0;
        System.out.println("Took " + runTimeInSeconds + " seconds to run AnimeCrawlerController");
    }
}
