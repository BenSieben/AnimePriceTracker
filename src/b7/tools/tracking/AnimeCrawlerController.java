package b7.tools.tracking;

import b7.tools.DateTool;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public static final String SENTAI_FILMWORKS_CRAWLER_CSV_FILENAME = CSVS_PATH + DateTool.findCurrentDateString() + "_sentaifilmworks.csv";
    public static final String RIGHT_STUF_CRAWLER_CSV_FILENAME = CSVS_PATH + DateTool.findCurrentDateString() + "_rightstuf.csv";

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
    public boolean visitAllSentaiFilmworksPagesMultithreaded(boolean printProgress) {
        return sentaiFilmworksCrawler.visitAllPagesMultithreaded(printProgress);
    }

    /**
     * Uses the Right Stuf Crawler to visit all pages (to get updated pricing information)
     * @param printProgress true to print out found products to standard output, false to not print
     * @return true if all pages were successfully visited, false otherwise
     */
    public boolean visitAllRightStufPagesMultithreaded(boolean printProgress) {
        return rightStufCrawler.visitAllPagesMultithreaded(printProgress);
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
                animePriceTrackerGUI.changeBasicOperationMessageLabel("Please do not close the window until this message changes to \"Done!\"");

                // Spawn and execute a new SwingWorker to call the appropriate method, and restore GUI controls when done processing
                SwingWorker<Object, Object> runMethodWorker = new SwingWorker<Object, Object>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        parseBaseSentaiFilmworksPage();
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            // Re-enable all GUI components and reset System.out / System.err streams to defaults
                            animePriceTrackerGUI.stopRunBasicOperation();
                            animePriceTrackerGUI.changeBasicOperationMessageLabel("Done!");
                        }
                        catch (Exception ex) {
                            // Do nothing
                        }
                    }
                };
                runMethodWorker.execute();
            }
        });

        animePriceTrackerGUI.addParseBaseRightStufPageButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();
                animePriceTrackerGUI.changeBasicOperationMessageLabel("Please do not close the window until this message changes to \"Done!\"");

                // Spawn and execute a new SwingWorker to call the appropriate method, and restore GUI controls when done processing
                SwingWorker<Object, Object> runMethodWorker = new SwingWorker<Object, Object>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        parseBaseRightStufPage();
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            // Re-enable all GUI components and reset System.out / System.err streams to defaults
                            animePriceTrackerGUI.stopRunBasicOperation();
                            animePriceTrackerGUI.changeBasicOperationMessageLabel("Done!");
                        }
                        catch (Exception ex) {
                            // Do nothing
                        }
                    }
                };
                runMethodWorker.execute();
            }
        });

        animePriceTrackerGUI.addVisitAllSentaiFilmworksPagesButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();
                animePriceTrackerGUI.changeBasicOperationMessageLabel("Please do not close the window until this message changes to \"Done!\"");

                // Spawn and execute a new SwingWorker to call the appropriate method, and restore GUI controls when done processing
                SwingWorker<Object, Object> runMethodWorker = new SwingWorker<Object, Object>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        visitAllSentaiFilmworksPagesSingleThreaded(true);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            // Re-enable all GUI components and reset System.out / System.err streams to defaults
                            animePriceTrackerGUI.stopRunBasicOperation();
                            animePriceTrackerGUI.changeBasicOperationMessageLabel("Done!");
                        }
                        catch (Exception ex) {
                            // Do nothing
                        }
                    }
                };
                runMethodWorker.execute();
            }
        });

        animePriceTrackerGUI.addVisitAllRightStufPagesButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();
                animePriceTrackerGUI.changeBasicOperationMessageLabel("Please do not close the window until this message changes to \"Done!\"");

                // Spawn and execute a new SwingWorker to call the appropriate method, and restore GUI controls when done processing
                SwingWorker<Object, Object> runMethodWorker = new SwingWorker<Object, Object>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        visitAllRightStufPagesSingleThreaded(true);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            // Re-enable all GUI components and reset System.out / System.err streams to defaults
                            animePriceTrackerGUI.stopRunBasicOperation();
                            animePriceTrackerGUI.changeBasicOperationMessageLabel("Done!");
                        }
                        catch (Exception ex) {
                            // Do nothing
                        }
                    }
                };
                runMethodWorker.execute();
            }
        });

        animePriceTrackerGUI.addUpdateAllCrawlDataButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();
                animePriceTrackerGUI.changeBasicOperationMessageLabel("Please do not close the window until this message changes to \"Done!\"");

                // Spawn and execute a new SwingWorker to call the appropriate method, and restore GUI controls when done processing
                SwingWorker<Object, Object> runMethodWorker = new SwingWorker<Object, Object>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        runAnimeCrawlerControllerPriceUpdate(true, true);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            // Re-enable all GUI components and reset System.out / System.err streams to defaults
                            animePriceTrackerGUI.stopRunBasicOperation();
                            animePriceTrackerGUI.changeBasicOperationMessageLabel("Done!");
                        }
                        catch (Exception ex) {
                            // Do nothing
                        }
                    }
                };
                runMethodWorker.execute();
            }
        });

        animePriceTrackerGUI.addUpdateSentaiFilmworksCrawlDataButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();
                animePriceTrackerGUI.changeBasicOperationMessageLabel("Please do not close the window until this message changes to \"Done!\"");

                // Spawn and execute a new SwingWorker to call the appropriate method, and restore GUI controls when done processing
                SwingWorker<Object, Object> runMethodWorker = new SwingWorker<Object, Object>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        runAnimeCrawlerControllerPriceUpdate(true, false);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            // Re-enable all GUI components and reset System.out / System.err streams to defaults
                            animePriceTrackerGUI.stopRunBasicOperation();
                            animePriceTrackerGUI.changeBasicOperationMessageLabel("Done!");
                        }
                        catch (Exception ex) {
                            // Do nothing
                        }
                    }
                };
                runMethodWorker.execute();
            }
        });

        animePriceTrackerGUI.addUpdateRightStufCrawlDataButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();
                animePriceTrackerGUI.changeBasicOperationMessageLabel("Please do not close the window until this message changes to \"Done!\"");

                // Spawn and execute a new SwingWorker to call the appropriate method, and restore GUI controls when done processing
                SwingWorker<Object, Object> runMethodWorker = new SwingWorker<Object, Object>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        runAnimeCrawlerControllerPriceUpdate(false, true);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            // Re-enable all GUI components and reset System.out / System.err streams to defaults
                            animePriceTrackerGUI.stopRunBasicOperation();
                            animePriceTrackerGUI.changeBasicOperationMessageLabel("Done!");
                        }
                        catch (Exception ex) {
                            // Do nothing
                        }
                    }
                };
                runMethodWorker.execute();
            }
        });

        animePriceTrackerGUI.addMakeCsvsButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Set up GUI for running a method (lock down everything & capture output to text area)
                animePriceTrackerGUI.startRunBasicOperation();
                animePriceTrackerGUI.changeBasicOperationMessageLabel("Please do not close the window until this message changes to \"Done!\"");

                // Spawn and execute a new SwingWorker to call the appropriate method, and restore GUI controls when done processing
                SwingWorker<Object, Object> runMethodWorker = new SwingWorker<Object, Object>() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        makeExcelCSVsForAnimeCrawlerControllerCrawlData();
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            // Re-enable all GUI components and reset System.out / System.err streams to defaults
                            animePriceTrackerGUI.stopRunBasicOperation();
                            animePriceTrackerGUI.changeBasicOperationMessageLabel("Done!");
                        }
                        catch (Exception ex) {
                            // Do nothing
                        }
                    }
                };
                runMethodWorker.execute();
            }
        });

        // Set up buttons on the graph product panel
        // Create product buttons
        final List<JRadioButton> sentaiFilmworksProductButtons = createProductButtons(
                sentaiFilmworksCrawler.getCrawlData(), animePriceTrackerGUI);
        final List<JRadioButton> rightStufProductButtons = createProductButtons(
                rightStufCrawler.getCrawlData(), animePriceTrackerGUI);

        // Create website buttons
        final List<JRadioButton> websiteButtons = createWebsiteButtons(
                sentaiFilmworksProductButtons, rightStufProductButtons, animePriceTrackerGUI);

        // Set Sentai Filmworks website (and first product in the Sentai Filmworks products) as selected
        //websiteButtons.get(0).setSelected(true);
        //sentaiFilmworksProductButtons.get(0).setSelected(true);

        // Set the lists to be the initial buttons
        animePriceTrackerGUI.changeSelectWebsiteButtonGroupButtons(websiteButtons);
        animePriceTrackerGUI.changeSelectProductButtonGroupButtons(sentaiFilmworksProductButtons);

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
     * Creates product JRadioButtons in a List to send to the select product from websites panel
     * @param crawlData the data to get products from
     * @param gui the AnimePriceTrackerGUI to set product buttons for
     * @return a list of JRadioButtons to use to list out all products from the given crawl data
     */
    private List<JRadioButton> createProductButtons(CrawlData crawlData, final AnimePriceTrackerGUI gui) {
        List<JRadioButton> productButtons = new ArrayList<>();

        // Make product buttons from the given crawl data
        Map<String, Product> crawlDataProducts = crawlData.getProductMap();
        Set<String> crawlDataKeys = crawlDataProducts.keySet();
        for(String key : crawlDataKeys) {
            final Product currentProduct = crawlDataProducts.get(key);
            JRadioButton buttonToAdd = new JRadioButton(currentProduct.getProductName());
            buttonToAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Each product button changes the product that gets graphed on the line graph panel
                    gui.changeProductLineGraphPanelProduct(currentProduct);
                    gui.repaint();
                }
            });
            productButtons.add(buttonToAdd);
        }

        return productButtons;
    }

    /**
     * Creates website JRadioButtons in a List to send to the select product from websites panel
     * @param sentaiFilmworksProducts list of JRadioButtons associated with Sentai Filmworks products
     * @param rightStufProducts list of JRadioButtons associated with Right Stuf products
     * @param gui the AnimePriceTrackerGUI to set website buttons for
     */
    private List<JRadioButton> createWebsiteButtons(final List<JRadioButton> sentaiFilmworksProducts,
                                                    final List<JRadioButton> rightStufProducts,
                                                    final AnimePriceTrackerGUI gui) {
        List<JRadioButton> websiteButtons = new ArrayList<>();

        // Create Sentai Filmworks button
        JRadioButton sentaiFilmworksWebsiteButton = new JRadioButton("Sentai Filmworks");
        sentaiFilmworksWebsiteButton.setSelected(true);
        sentaiFilmworksWebsiteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Change product list to be Sentai Filmworks products on GUI
                gui.changeSelectProductButtonGroupButtons(sentaiFilmworksProducts);
            }
        });

        // Create Right Stuf button
        JRadioButton rightStufWebsiteButton = new JRadioButton("Right Stuf");
        rightStufWebsiteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Change product list to be Right Stuf products on GUI
                gui.changeSelectProductButtonGroupButtons(rightStufProducts);
            }
        });

        // Add buttons to the list and return it
        websiteButtons.add(sentaiFilmworksWebsiteButton);
        websiteButtons.add(rightStufWebsiteButton);
        return websiteButtons;
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
     * @param printProgress whether or not to print progress of the crawler as it runs
     * @return true if visiting all pages worked without issue, false if an error occurred during the process
     */
    public boolean visitAllSentaiFilmworksPagesSingleThreaded(boolean printProgress) {
        return sentaiFilmworksCrawler.visitAllPages(printProgress);
    }

    /**
     * Sets up code to call methods to traverse all pages for Right Stuf store
     * (but not load or save found results). Will print progress during page crawling
     * @param printProgress whether or not to print progress of the crawler as it runs
     * @return true if visiting all pages worked without issue, false if an error occurred during the process
     */
    public boolean visitAllRightStufPagesSingleThreaded(boolean printProgress) {
        return rightStufCrawler.visitAllPages(printProgress);
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
     * for AnimeCrawlerController.visitAllSentaiFilmworksPagesSingleThreaded() to print out progress
     * during page crawling
     * @param updateSentaiFilmworks true to update price information for Sentai Filmworks listings, false to not
     * @param updateRightStuf true to update price information for Right Stuf listing, false to not
     */
    public void runAnimeCrawlerControllerPriceUpdate(boolean updateSentaiFilmworks, boolean updateRightStuf) {
        // Load existing data and try to update that information
        long startTime = System.currentTimeMillis();

        // Visit Sentai Filmworks if boolean argument is set to true
        if(updateSentaiFilmworks) {
            runSentaiFilmworksCrawlerPriceUpdateMultithreaded();
        }

        // Visit Right Stuf if boolean argument is set to true
        if(updateRightStuf) {
            runRightStufCrawlerPriceUpdateMultithreaded();
        }
    }

    /**
     * Updates Sentai Filmworks Crawler with current price information and saves
     * the new information (multithreaded version)
     */
    private void runSentaiFilmworksCrawlerPriceUpdateMultithreaded() {
        // Load existing data and try to update that information
        long startTime = System.currentTimeMillis();

        // Visit Sentai Filmworks
        boolean visitSuccessful = visitAllSentaiFilmworksPagesMultithreaded(true);
        if(visitSuccessful) {
            System.out.println("\nVisiting all pages worked for Sentai Filmworks!\n");
        }
        else {
            System.out.println("\nVisiting all pages for Sentai Filmworks failed (likely accessing too many pages too rapidly on website)\n");
        }

        // Save the updated information back to file
        saveSentaiFilmworksCrawler(SENTAI_FILMWORKS_CRAWLER_FILENAME);
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        double runTimeInSeconds = runTime / 1000.0;
        System.out.println("\nTook " + runTimeInSeconds + " seconds to run price update for Sentai Filmworks");
    }

    /**
     * Updates Sentai Filmworks Crawler with current price information and saves
     * the new information (multithreaded version)
     */
    private void runSentaiFilmworksCrawlerPriceUpdateSingleThreaded() {
        // Load existing data and try to update that information
        long startTime = System.currentTimeMillis();

        // Visit Sentai Filmworks
        boolean visitSuccessful = visitAllSentaiFilmworksPagesSingleThreaded(true);
        if(visitSuccessful) {
            System.out.println("\nVisiting all pages worked for Sentai Filmworks!\n");
        }
        else {
            System.out.println("\nVisiting all pages for Sentai Filmworks failed (likely accessing too many pages too rapidly on website)\n");
        }

        // Save the updated information back to file
        saveSentaiFilmworksCrawler(SENTAI_FILMWORKS_CRAWLER_FILENAME);
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        double runTimeInSeconds = runTime / 1000.0;
        System.out.println("\nTook " + runTimeInSeconds + " seconds to run price update for Sentai Filmworks");
    }

    /**
     * Updates Right Stuf Crawler with current price information and saves
     * the new information (multithreaded version)
     */
    private void runRightStufCrawlerPriceUpdateMultithreaded() {
        // Load existing data and try to update that information
        long startTime = System.currentTimeMillis();

        // Visit Right Stuf
        boolean visitSuccessful = visitAllRightStufPagesMultithreaded(true);
        if(visitSuccessful) {
            System.out.println("\nVisiting all pages worked for Right Stuf!\n");
        }
        else {
            System.out.println("\nVisiting all pages for Right Stuf failed (likely accessing too many pages too rapidly on website)\n");
        }

        // Save the updated information back to file
        saveRightStufCrawler(RIGHT_STUF_CRAWLER_FILENAME);
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        double runTimeInSeconds = runTime / 1000.0;
        System.out.println("\nTook " + runTimeInSeconds + " seconds to run price update for Right Stuf");
    }

    /**
     * Updates Right Stuf Crawler with current price information and
     * saves the new information (single threaded version)
     */
    public void runRightStufCrawlerPriceUpdateSingleThreaded() {
        // Load existing data and try to update that information
        long startTime = System.currentTimeMillis();

        // Visit Right Stuf
        boolean visitSuccessful = visitAllRightStufPagesSingleThreaded(true);
        if(visitSuccessful) {
            System.out.println("\nVisiting all pages worked for Right Stuf!\n");
        }
        else {
            System.out.println("\nVisiting all pages for Right Stuf failed (likely accessing too many pages too rapidly on website)\n");
        }

        // Save the updated information back to file
        saveRightStufCrawler(RIGHT_STUF_CRAWLER_FILENAME);
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        double runTimeInSeconds = runTime / 1000.0;
        System.out.println("\nTook " + runTimeInSeconds + " seconds to run price update for Right Stuf");
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
