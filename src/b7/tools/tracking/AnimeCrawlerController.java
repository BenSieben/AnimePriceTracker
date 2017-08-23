package b7.tools.tracking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        // Create new AnimePriceTrackerGUI
        final AnimePriceTrackerGUI animePriceTrackerGUI = new AnimePriceTrackerGUI();

        // Add action listeners for the buttons on the basic operations panel
        animePriceTrackerGUI.addParseBaseSentaiFilmworksPageButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();

                // TODO actually perform action
                animePriceTrackerGUI.changeBasicOperationMessageLabel("addParseBaseSentaiFilmworksPageButtonActionListener");

                // Re-enable all GUI components and reset System.out / System.err streams to defaults
                animePriceTrackerGUI.stopRunBasicOperation();
            }
        });
        animePriceTrackerGUI.addParseBaseRightStufPageButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();

                // TODO actually perform action
                animePriceTrackerGUI.changeBasicOperationMessageLabel("addParseBaseRightStufPageButtonActionListener");

                // Re-enable all GUI components and reset System.out / System.err streams to defaults
                animePriceTrackerGUI.stopRunBasicOperation();
            }
        });
        animePriceTrackerGUI.addVisitAllSentaiFilmworksPagesButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();

                // TODO actually perform action
                animePriceTrackerGUI.changeBasicOperationMessageLabel("addVisitAllSentaiFilmworksPagesButtonActionListener");

                // Re-enable all GUI components and reset System.out / System.err streams to defaults
                animePriceTrackerGUI.stopRunBasicOperation();
            }
        });
        animePriceTrackerGUI.addVisitAllRightStufPagesButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();

                // TODO actually perform action
                animePriceTrackerGUI.changeBasicOperationMessageLabel("addVisitAllRightStufPagesButtonActionListener");

                // Re-enable all GUI components and reset System.out / System.err streams to defaults
                animePriceTrackerGUI.stopRunBasicOperation();
            }
        });
        animePriceTrackerGUI.addUpdateCrawlDataButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();

                // TODO actually perform action
                animePriceTrackerGUI.changeBasicOperationMessageLabel("addUpdateCrawlDataButtonActionListener");

                // Re-enable all GUI components and reset System.out / System.err streams to defaults
                animePriceTrackerGUI.stopRunBasicOperation();
            }
        });
        animePriceTrackerGUI.addMakeCsvsButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();

                // TODO actually perform action
                animePriceTrackerGUI.changeBasicOperationMessageLabel("addMakeCsvsButtonActionListener");

                // Re-enable all GUI components and reset System.out / System.err streams to defaults
                animePriceTrackerGUI.stopRunBasicOperation();
            }
        });

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

    /**
     * Sets up code to call methods to parse the base Sentai Filmworks store page
     */
    public void parseBaseSentaiFilmworksPage() {
        sentaiFilmworksCrawler.saveBasePage(true);
        sentaiFilmworksCrawler.parseBasePage();
    }

    /**
     * Sets up code to call methods to parse the base Right Stuf store page
     */
    public void parseBaseRightStufPage() {
        rightStufCrawler.saveBasePage(true);
        rightStufCrawler.parseBasePage();
    }

    /**
     * Sets up code to call methods to traverse all pages for Sentai Filmworks store
     * (but not load or save found results). Will print progress during page crawling
     */
    public void visitAllSentaiFilmworksPages() {
        sentaiFilmworksCrawler.visitAllPages(true);
    }

    /**
     * Sets up code to call methods to traverse all pages for Right Stuf store
     * (but not load or save found results). Will print progress during page crawling
     */
    public void visitAllRightStufPages() {
        rightStufCrawler.visitAllPages(true);
    }

    /**
     * Sets up code to call methods to save and parse some sample product pages for Sentai Filmworks
     */
    public void parseSampleSentaiFilmworksProductPages() {
        // Pick some sample product pages
        String productURL1 = "https://shop.sentaifilmworks.com/products/young-black-jack-complete-collection";
        String productURL2 = "https://shop.sentaifilmworks.com/products/yumeria-complete-collection";
        String productURL3 = "https://shop.sentaifilmworks.com/products/yuyushiki-complete-collection";

        // Save those product pages (do not re-get them if they already exist)
        sentaiFilmworksCrawler.saveProductPage(productURL1, true);
        sentaiFilmworksCrawler.saveProductPage(productURL2, true);
        sentaiFilmworksCrawler.saveProductPage(productURL3, true);

        // Parse those product pages
        sentaiFilmworksCrawler.parseProductPage(productURL1);
        sentaiFilmworksCrawler.parseProductPage(productURL2);
        sentaiFilmworksCrawler.parseProductPage(productURL3);
    }

    /**
     * Runs the AnimeCrawlerController to load existing crawl data, visit all pages,
     * update information, and save the results back. Will set printProgress to true
     * for AnimeCrawlerController.visitAllSentaiFilmworksPages() to print out progress
     * during page crawling
     */
    public void runAnimeCrawlerControllerPriceUpdate() {
        // Load existing data and try to update that information
        long startTime = System.currentTimeMillis();

        // Visit Sentai Filmworks
        boolean visitSuccessful = visitAllSentaiFilmworksPages(true);
        if(visitSuccessful) {
            System.out.println("\nVisiting all pages worked for Sentai Filmworks!\n");
        }
        else {
            System.out.println("\nVisiting all pages for Sentai Filmworks failed (likely accessing too many pages too rapidly on website)\n");
        }

        // Visit Right Stuf
        visitSuccessful = visitAllRightStufPages(true);
        if(visitSuccessful) {
            System.out.println("\nVisiting all pages worked for Right Stuf!\n");
        }
        else {
            System.out.println("\nVisiting all pages for Right Stuf failed (likely accessing too many pages too rapidly on website)\n");
        }

        // Save the updated information back to file
        saveSentaiFilmworksCrawler(SENTAI_FILMWORKS_CRAWLER_FILENAME);
        saveRightStufCrawler(RIGHT_STUF_CRAWLER_FILENAME);
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        double runTimeInSeconds = runTime / 1000.0;
        System.out.println("\nTook " + runTimeInSeconds + " seconds to run AnimeCrawlerController");
    }

    /**
     * Saves crawl data in CSV format back to a file
     */
    public void makeExcelCSVsForAnimeCrawlerControllerCrawlData() {
        // Save Sentai Filmworks CSV
        saveSentaiFilmworksCrawlDataToExcelCSV(SENTAI_FILMWORKS_CRAWLER_CSV_FILENAME);

        // Save Right Stuf CSV
        saveRightStufCrawlDataToExcelCSV(RIGHT_STUF_CRAWLER_CSV_FILENAME);
    }
}
