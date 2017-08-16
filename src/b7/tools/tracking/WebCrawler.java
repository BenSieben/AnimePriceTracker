package b7.tools.tracking;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

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
     * Uses the headless browser PhantomJS (version 2.1 used in development, so version 2.0+ recommended)
     * to read a given URL with PhantomJS's Web Page Module
     * @param URL the URL to open
     * @param phantomJSPath the path to PhantomJS executable that can be used to run PhantomJS on a JavaScript file
     *                      (pass null to use the phantomjs.exe in PHANTOM_JS_WINDOWS_EXE_PATH)
     * @return the HTML code of the URL after being loaded with JavaScript, or null if an error occurs
     */
    public static String readUrlContentsWithJavaScript(String URL, String phantomJSPath) {
        // Use a StringBuilder to efficiently append all the page contents that gets returned at the end of the method
        StringBuilder stringBuilder = new StringBuilder();
        try{
            // Use PhantomJS with a script that simply prints the loaded HTML of a given URL argument
            if(phantomJSPath == null) {  // If path is null, we assume user wants to use default Windows executable
                phantomJSPath = PHANTOM_JS_WINDOWS_EXE_PATH;
            }
            String executeCommand = phantomJSPath + " " + LOAD_PAGE_JS_FILE_PATH + " " + URL;
            // With terminal / command line in root folder of repository, command looks like:
            //   ./bin/phantomjs-2.1.1-windows/phantomjs.exe ./scripts/loadpage.js <URL>
            System.out.println("Executing " + executeCommand);
            Process process = Runtime.getRuntime().exec(executeCommand);
            InputStream inputStream = process.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            String line;
            do {
                line = scanner.nextLine();
                System.out.println(line);
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            while(scanner.hasNextLine());
        }
        catch(IOException ex) {
            System.err.println("[ERROR] IOException when getting page with PhantomJS for URL " + URL);
            ex.printStackTrace();
            return null;
        }
        return stringBuilder.toString();
    }

}
