package b7.tools.tracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Basic class to represent a web crawler
 */
public class WebCrawler {

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
    public String getInitialURLContents() {
        // Use a StringBuilder to efficiently append all the page contents that gets returned at the end of the method
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(initialURL);
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
        }
        catch(MalformedURLException ex) {
            System.err.println("Malformed URL " + initialURL + " could not be instantiated");
            ex.printStackTrace();
        }
        catch(IOException ex) {
            System.err.println("Could not open a connection to " + initialURL);
            ex.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
