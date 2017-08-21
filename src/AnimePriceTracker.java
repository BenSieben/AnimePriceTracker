import b7.tools.tracking.AnimeCrawlerController;
import b7.tools.tracking.RightStufCrawler;
import b7.tools.tracking.SentaiFilmworksCrawler;

import java.util.Scanner;

/**
 * Class with main method to run the program to get price information
 * for anime from online stores
 */
public class AnimePriceTracker {

    // Integers which correspond to specific command line interface commands
    public static final int EXIT_OPTION = 0;
    public static final int PARSE_BASE_SENTAI_FILMWORKS_PAGE = 1;
    public static final int PARSE_BASE_RIGHT_STUF_PAGE = 2;
    public static final int VISIT_ALL_SENTAI_FILMWORKS_PAGES = 3;
    public static final int VISIT_ALL_RIGHT_STUF_PAGES = 4;
    public static final int UPDATE_CRAWL_DATA = 5;
    public static final int MAKE_CSVS = 6;
    public static final int OPEN_GUI = 7;

    public static void main(String[] args) {
        // Check if user specified argument "gui" to indicate to directly open the GUI
        if(args.length >= 1 && "gui".compareTo(args[0].toLowerCase()) == 0) {
            openAnimePriceTrackerGUI(false);
            return;
        }

        // Run the command line interface
        runCommandLineInterface();

        // parseSampleSentaiFilmworksProductPages() is not used because product pages are no longer visited
        // However, it will be left implemented since the code already exists / works
        //parseSampleSentaiFilmworksProductPages();
    }

    /**
     * Runs a command line interface for users to interact with the program
     * by executing chosen commands
     */
    private static void runCommandLineInterface() {
        Scanner scanner = new Scanner(System.in);
        int option = -1;
        while(option != EXIT_OPTION) {
            // Print options to user
            printCommandLineInterfacePrompt();

            // Make sure user passes an actual number
            try {
                option = Integer.parseInt(scanner.nextLine());
            }
            catch(NumberFormatException ex) {
                System.err.println("[ERROR] An invalid command was entered; please enter a valid command\n");
                continue;
            }

            // Make sure number passed by user is a valid option
            switch(option) {
                case PARSE_BASE_SENTAI_FILMWORKS_PAGE:
                    parseBaseSentaiFilmworksPage();
                    break;
                case PARSE_BASE_RIGHT_STUF_PAGE:
                    parseBaseRightStufPage();
                    break;
                case VISIT_ALL_SENTAI_FILMWORKS_PAGES:
                    visitAllSentaiFilmworksPages();
                    break;
                case VISIT_ALL_RIGHT_STUF_PAGES:
                    visitAllRightStufPages();
                    break;
                case UPDATE_CRAWL_DATA:
                    runAnimeCrawlerControllerPriceUpdate();
                    break;
                case MAKE_CSVS:
                    makeExcelCSVsForAnimeCrawlerControllerCrawlData();
                    break;
                case OPEN_GUI:
                    openAnimePriceTrackerGUI(true);
                    break;
                case EXIT_OPTION:
                    break;
                default: {
                    System.out.println("[ERROR] Invalid number entered\n");
                }
            }
            System.out.println();
        }
        scanner.close();
    }

    /**
     * Prints general prompt listing what actions user can make
     * on the command line
     */
    private static void printCommandLineInterfacePrompt() {
        System.out.println("***** Anime Price Tracker *****");
        System.out.println("Please select from an option below:");
        System.out.println(getCommandString(PARSE_BASE_SENTAI_FILMWORKS_PAGE, "Parse base Sentai Filmworks page"));
        System.out.println(getCommandString(PARSE_BASE_RIGHT_STUF_PAGE , "Parse base Right Stuf page"));
        System.out.println(getCommandString(VISIT_ALL_SENTAI_FILMWORKS_PAGES, "Visit all Sentai Filmworks pages"));
        System.out.println(getCommandString(VISIT_ALL_RIGHT_STUF_PAGES, "Visit all Right Stuf pages"));
        System.out.println(getCommandString(UPDATE_CRAWL_DATA, "Update crawl data"));
        System.out.println(getCommandString(MAKE_CSVS, "Generate CSVs from crawl data"));
        System.out.println(getCommandString(OPEN_GUI, "Open price tracker GUI"));
        System.out.println(getCommandString(EXIT_OPTION, "Exit the program"));
        System.out.print("--> ");
    }

    // Returns a simple command string from a given value and describing text of the command
    private static String getCommandString(int commandValue, String commandText) {
        return "[" + commandValue + "] " + commandText;
    }

    /**
     * Sets up code to call methods to parse the base Sentai Filmworks store page
     */
    private static void parseBaseSentaiFilmworksPage() {
        SentaiFilmworksCrawler sentaiFilmworksCrawler = new SentaiFilmworksCrawler();
        sentaiFilmworksCrawler.saveBasePage(true);
        sentaiFilmworksCrawler.parseBasePage();
    }

    /**
     * Sets up code to call methods to parse the base Right Stuf store page
     */
    private static void parseBaseRightStufPage() {
        RightStufCrawler rightStufCrawler = new RightStufCrawler();
        rightStufCrawler.saveBasePage(true);
        rightStufCrawler.parseBasePage();
    }

    /**
     * Sets up code to call methods to traverse all pages for Sentai Filmworks store
     * (but not load or save found results). Will print progress during page crawling
     */
    private static void visitAllSentaiFilmworksPages() {
        SentaiFilmworksCrawler sentaiFilmworksCrawler = new SentaiFilmworksCrawler();
        sentaiFilmworksCrawler.visitAllPages(true);
    }

    /**
     * Sets up code to call methods to traverse all pages for Right Stuf store
     * (but not load or save found results). Will print progress during page crawling
     */
    private static void visitAllRightStufPages() {
        RightStufCrawler rightStufCrawler = new RightStufCrawler();
        rightStufCrawler.visitAllPages(true);
    }

    /**
     * Sets up code to call methods to save and parse some sample product pages for Sentai Filmworks
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
     * Runs the AnimeCrawlerController to load existing crawl data, visit all pages,
     * update information, and save the results back. Will set printProgress to true
     * for AnimeCrawlerController.visitAllSentaiFilmworksPages() to print out progress
     * during page crawling
     */
    private static void runAnimeCrawlerControllerPriceUpdate() {
        // Load existing data and try to update that information
        long startTime = System.currentTimeMillis();
        AnimeCrawlerController animeCrawlerController = new AnimeCrawlerController(
                AnimeCrawlerController.SENTAI_FILMWORKS_CRAWLER_FILENAME,
                AnimeCrawlerController.RIGHT_STUF_CRAWLER_FILENAME
        );

        // Visit Sentai Filmworks
        boolean visitSuccessful = animeCrawlerController.visitAllSentaiFilmworksPages(true);
        if(visitSuccessful) {
            System.out.println("\nVisiting all pages worked for Sentai Filmworks!\n");
        }
        else {
            System.out.println("\nVisiting all pages for Sentai Filmworks failed (likely accessing too many pages too rapidly on website)\n");
        }

        // Visit Right Stuf
        visitSuccessful = animeCrawlerController.visitAllRightStufPages(true);
        if(visitSuccessful) {
            System.out.println("\nVisiting all pages worked for Right Stuf!\n");
        }
        else {
            System.out.println("\nVisiting all pages for Right Stuf failed (likely accessing too many pages too rapidly on website)\n");
        }

        // Save the updated information back to file
        animeCrawlerController.saveSentaiFilmworksCrawler(AnimeCrawlerController.SENTAI_FILMWORKS_CRAWLER_FILENAME);
        animeCrawlerController.saveRightStufCrawler(AnimeCrawlerController.RIGHT_STUF_CRAWLER_FILENAME);
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        double runTimeInSeconds = runTime / 1000.0;
        System.out.println("\nTook " + runTimeInSeconds + " seconds to run AnimeCrawlerController");
    }

    /**
     * Runs AnimeCrawlerController to load existing crawl data and then save it in CSV format back to a file
     */
    private static void makeExcelCSVsForAnimeCrawlerControllerCrawlData() {
        AnimeCrawlerController animeCrawlerController = new AnimeCrawlerController(
                AnimeCrawlerController.SENTAI_FILMWORKS_CRAWLER_FILENAME,
                AnimeCrawlerController.RIGHT_STUF_CRAWLER_FILENAME
        );

        // Save Sentai Filmworks CSV
        animeCrawlerController.saveSentaiFilmworksCrawlDataToExcelCSV(AnimeCrawlerController.SENTAI_FILMWORKS_CRAWLER_CSV_FILENAME);

        // Save Right Stuf CSV
        animeCrawlerController.saveRightStufCrawlDataToExcelCSV(AnimeCrawlerController.RIGHT_STUF_CRAWLER_CSV_FILENAME);
    }

    /**
     * Starts the GUI of the anime price tracker
     * @param fromCLI true if opened from CLI, false if opened directly (determines
     *                whether or not we give message to tell user to quit GUI to return to CLI)
     */
    private static void openAnimePriceTrackerGUI(boolean fromCLI) {
        AnimeCrawlerController animeCrawlerController = new AnimeCrawlerController(
                AnimeCrawlerController.SENTAI_FILMWORKS_CRAWLER_FILENAME,
                AnimeCrawlerController.RIGHT_STUF_CRAWLER_FILENAME
        );
        if(fromCLI) {
            System.out.println("Exit the GUI to get back to the prompt of the command line interface...");
        }
        animeCrawlerController.openGUI();
    }
}
