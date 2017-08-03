package b7.tools.tracking;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;

/**
 * WebCrawler that is specifically customized for the Anime
 * listings on Sentai Filmwork's online store
 */
public class SentaiFilmworksCrawler extends WebCrawler {

    // The base URL to start crawling from
    public final static String INITIAL_URL = "https://shop.sentaifilmworks.com/collections/shows?page=1";

    // Path we will save the test base page in (so we can create directory if it doesn't already exist)
    public final static String BASE_PAGE_PATH = "savedata/basepages/";

    // Where we will save the test base page
    public final static String BASE_PAGE_NAME = BASE_PAGE_PATH + "sentaifilmworks.html";

    /**
     * Creates a new SentaiFilmworksCrawler
     */
    public SentaiFilmworksCrawler() {
        super(INITIAL_URL);
    }

    /**
     * Saves the HTML code of the INITIAL_URL contents to a local file
     * (BASE_PAGE_NAME)
     * @param getDataAgain true to retrieve the INITIAL_URL contents even if the base page is already detected,
     *                     false to not retrieve new contents when the base page is already detected
     */
    public void saveBasePage(boolean getDataAgain) {
        // If user does not want to re-obtain data in case of base page already existing, then
        //   check for existing base page file and return if it exists
        if(!getDataAgain) {
            File file = new File(BASE_PAGE_NAME);
            if(file.exists()) {
                return;
            }
        }

        String fullPageHTML = super.getInitialURLContents();
        BufferedWriter bufferedWriter = null;
        try {
            File file = new File(BASE_PAGE_PATH);
            if(!file.exists()) {
                file.mkdirs();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(BASE_PAGE_NAME));
            bufferedWriter.write(fullPageHTML);
            bufferedWriter.close();  // Close here instead of "finally" block, as it might throw IOException
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Attempts to parse the base page (created with saveBasePage() method)
     */
    public void parseBasePage() {
        try {
            // Read in the file
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(BASE_PAGE_NAME));
            String currentLine = bufferedReader.readLine();
            while(currentLine != null) {
                stringBuilder.append(currentLine);
                stringBuilder.append("\n");
                currentLine = bufferedReader.readLine();
            }
            bufferedReader.close();

            // Use Jsoup to start parsing the HTML code of the base page
            Document document = Jsoup.parse(stringBuilder.toString());
        }
        catch(FileNotFoundException ex) {
            System.err.println("[ERROR] Could not find file " + BASE_PAGE_NAME + ", make sure it has been created!");
            ex.printStackTrace();
        }
        catch(IOException ex) {
            System.err.println("[ERROR] Could not close reader going through " + BASE_PAGE_NAME);
            ex.printStackTrace();
        }
    }

}
