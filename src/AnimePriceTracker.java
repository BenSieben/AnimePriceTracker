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

    // Controller instance variable
    private static AnimeCrawlerController animeCrawlerController = new AnimeCrawlerController(
            AnimeCrawlerController.SENTAI_FILMWORKS_CRAWLER_FILENAME,
            AnimeCrawlerController.RIGHT_STUF_CRAWLER_FILENAME
    );

    public static void main(String[] args) {
        // Check if user specified argument "gui" to indicate to directly open the GUI
        if(args.length >= 1 && "gui".compareTo(args[0].toLowerCase()) == 0) {
            animeCrawlerController.openGUI();
        }
        else {
            // Run the command line interface
            runCommandLineInterface();
        }

        // Force program to exit (terminate any background processes which might still be running)
        System.exit(0);
    }

    /**
     * Runs a command line interface for users to interact with the program
     * by executing chosen commands
     */
    protected static void runCommandLineInterface() {
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
                    animeCrawlerController.parseBaseSentaiFilmworksPage();
                    break;
                case PARSE_BASE_RIGHT_STUF_PAGE:
                    animeCrawlerController.parseBaseRightStufPage();
                    break;
                case VISIT_ALL_SENTAI_FILMWORKS_PAGES:
                    animeCrawlerController.visitAllSentaiFilmworksPages();
                    break;
                case VISIT_ALL_RIGHT_STUF_PAGES:
                    animeCrawlerController.visitAllRightStufPages();
                    break;
                case UPDATE_CRAWL_DATA:
                    animeCrawlerController.runAnimeCrawlerControllerPriceUpdate(true, true);
                    break;
                case MAKE_CSVS:
                    animeCrawlerController.makeExcelCSVsForAnimeCrawlerControllerCrawlData();
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
        System.out.println(getCommandString(UPDATE_CRAWL_DATA, "Update crawl data for Sentai Filmworks and Right Stuf"));
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
     * Starts the GUI of the anime price tracker
     * @param fromCLI true if opened from CLI, false if opened directly (determines
     *                whether or not we give message to tell user to quit GUI to return to CLI)
     */
    private static void openAnimePriceTrackerGUI(boolean fromCLI) {
        if(fromCLI) {
            System.out.println("Exit the GUI to get back to the prompt of the command line interface...");
        }
        animeCrawlerController.openGUI();
    }
}
