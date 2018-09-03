package b7.tools.tracking;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Basic class to represent a web crawler
 */
public class WebCrawler {

    // Location of the loadpage.js file which uses PhantomJS to load a URL with JavaScript and print resulting HTML
    public static final String LOAD_PAGE_JS_FILE_PATH = "./scripts/loadpage.js";

    // Location of Windows phantomJS
    public static final String PHANTOM_JS_WINDOWS_EXE_PATH = "./bin/phantomjs-2.1.1-windows/phantomjs.exe";

    private String initialURL;  // Where to start crawling from

    /**
     * Constructs a new WebCrawler
     * @param initialURL the URL to start crawling from
     */
    public WebCrawler(String initialURL) {
        this.initialURL = initialURL;
    }

    /**
     * Returns the initial URL associated with this WebCrawler
     * @return the initial URL associated with the WebCrawler
     */
    public String getInitialURL() {
        return initialURL;
    }

    /**
     * Sets the initial URL of the WebCrawler to the given URL
     * @param newURL the new URL to use as the initial URL of the WebCrawler
     */
    public void setInitialURL(String newURL) {
        if(newURL != null) {
            initialURL = newURL;
        }
    }

    /**
     * Returns string of initial URL contents (HTML code)
     * @return string of HTML code at the initial URL
     */
    public String readInitialURLContents() {
        return readUrlContents(initialURL);
    }

    /**
     * Reads the given URL and returns the HTML on that page
     * @param URL the URL to read the contents of
     * @return the contents of the URL, or null if an exception occurred
     */
    public static String readUrlContents(String URL) {
        // Use a StringBuilder to efficiently append all the page contents that gets returned at the end of the method
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(URL);
            URLConnection urlConnection = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String currentLine;
            while(true) {
                currentLine = br.readLine();
                if(currentLine == null) {
                    break;
                }
                stringBuilder.append(currentLine);
                stringBuilder.append("\n");
            }
            br.close();
        }
        catch(MalformedURLException ex) {
            System.err.println("Malformed URL " + URL + " could not be instantiated");
            ex.printStackTrace();
            return null;
        }
        catch(IOException ex) {
            System.err.println("Could not open a connection to " + URL);
            ex.printStackTrace();
            return null;
        }
        return stringBuilder.toString();
    }

    /**
     * Uses htmlunit headless browser (version 2.27) to read a given URL with JavaScript
     * The htnlunit page will wait for all jobs on the page to finish before returning the HTML String back
     * Will attempt to load the page 10 times before returning an empty string
     * @param url the url to load
     * @return String of the contents of the page with all jobs fully completed, or empty string if page could not be successfully read
     */
    public static String readUrlContentsWithJavaScriptHtmlunit(String url) {
        return readUrlContentsWithJavaScriptHtmlunit(url, 1, 10, 60, 1);
    }

    /**
     * Uses htmlunit headless browser (version 2.27) to read a given URL with JavaScript
     * The htnlunit page will wait for all jobs on the page to finish before returning the HTML String back
     * @param url the url to load
     * @param attempt attempt number to load the url
     * @param maxAttempts the maximum amount
     * @param maxWaitSeconds the maximum amount of seconds to wait for the page to load its contents before returning it (default 60 if value <= 0 given)
     * @param minJobCount the amount of JavaScript jobs to allow remaining to load on the page before returning the page contents (default 1 if value <= 0 given)
     * @return String of the contents of the page with all jobs fully completed
     */
    public static String readUrlContentsWithJavaScriptHtmlunit(String url, int attempt, int maxAttempts, int maxWaitSeconds, int minJobCount) {
        // Turn off the error messages from htmlunit
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);

        // If bad attempt / maxAttempt numbers given throw Exception
        if (attempt < 1 || maxAttempts < 1) {
            throw new IllegalArgumentException("Invalid attempt number (" + attempt +
                    ") and / or maximum attempt number (" + maxAttempts + ") given");
        }
        // If maximum amount of attempts has been reached return empty string
        if (attempt > maxAttempts) {
            System.err.println("Maximum attempts (" + maxAttempts + ") reached for attempting to read url " + url + ", aborting attempts to read page");
            return "";
        }

        // Access the given url
        WebClient webClient = new WebClient();
        HtmlPage page = null;
        try {
            page = webClient.getPage(url);

            if (page == null) {
                System.err.println("Failed to get page \"" + url + "\" on attempt " + attempt + " of a maximum "
                        + maxAttempts + " attempts; trying again");
                attempt++;
            }
            else {
                // Wait for page to fully load before getting the final HTML code
                // From https://stackoverflow.com/questions/16956952/htmlunit-return-a-completely-loaded-page
                WebWindow pageWindow = page.getEnclosingWindow();
                if (pageWindow == null) {
                    return readUrlContentsWithJavaScriptHtmlunit(url);
                }

                JavaScriptJobManager manager = pageWindow.getJobManager();
                final int maxLoops = maxWaitSeconds <= 0 ? 60 : maxWaitSeconds;
                final int minJobs = minJobCount <= 0 ? 1 : minJobCount;
                int loop = 1;
                while (manager.getJobCount() > minJobs && loop <= maxLoops) {
                    try {
                        //System.out.println("On loop " + loop + ", jobs left is " + manager.getJobCount());
                        Thread.sleep(1000);
                        loop++;
                    }
                    catch(InterruptedException ex) {
                        ex.printStackTrace();
                        System.err.println("Failed to get page \"" + url + "\" on attempt " + attempt + " of a maximum "
                                + maxAttempts + " attempts; trying again");
                        return readUrlContentsWithJavaScriptHtmlunit(url, attempt + 1, maxAttempts, maxWaitSeconds, minJobCount);
                    }
                }

                // Return the page XML (i.e., HTML)
                System.err.println("Exited job loop at loop number " + (loop - 1) + " and job count " + manager.getJobCount() + " for url " + url);
                return page.asXml();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to get page \"" + url + "\" on attempt " + attempt + " of a maximum "
                    + maxAttempts + " attempts; trying again");
        }

        return readUrlContentsWithJavaScriptHtmlunit(url, attempt + 1, maxAttempts, maxWaitSeconds, minJobCount);  // If we get down here, an error occurred while getting page contents
    }

    /**
     * Uses the headless browser PhantomJS (version 2.1 used in development, so version 2.0+ recommended)
     * to read a given URL with PhantomJS's Web Page Module
     * @param URL the URL to open
     * @param phantomJSPath the path to PhantomJS executable that can be used to run PhantomJS on a JavaScript file
     *                      (pass null to use the phantomjs.exe in PHANTOM_JS_WINDOWS_EXE_PATH)
     * @param pageLoadWaitTime how long (in milliseconds) to wait for the page's JavaScript to load
     *                         (helpful to get full page contents for some websites which use a lot of JavaScript)
     * @return the HTML code of the URL after being loaded with JavaScript, or null if an error occurs
     */
    public static String readUrlContentsWithJavaScriptPhantomJS(String URL, String phantomJSPath, int pageLoadWaitTime) {
        // Use a StringBuilder to efficiently append all the page contents that gets returned at the end of the method
        StringBuilder stringBuilder = new StringBuilder();
        try{
            // Use PhantomJS with a script that simply prints the loaded HTML of a given URL argument
            if(phantomJSPath == null) {  // If path is null, we assume user wants to use default Windows executable
                phantomJSPath = PHANTOM_JS_WINDOWS_EXE_PATH;
            }
            String executeCommand = phantomJSPath + " " + LOAD_PAGE_JS_FILE_PATH + " " + URL + " " + pageLoadWaitTime;
            // With terminal / command line in root folder of repository, command looks like:
            //   ./bin/phantomjs-2.1.1-windows/phantomjs.exe ./scripts/loadpage.js <URL> <pageLoadWaitTime>
            Process process = Runtime.getRuntime().exec(executeCommand);
            InputStream inputStream = process.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            String line;
            do {
                line = scanner.nextLine();
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            while(scanner.hasNextLine());
            scanner.close();
        }
        catch(IOException ex) {
            System.err.println("[ERROR] IOException when getting page with PhantomJS for URL " + URL);
            ex.printStackTrace();
            return null;
        }
        return stringBuilder.toString();
    }

}
