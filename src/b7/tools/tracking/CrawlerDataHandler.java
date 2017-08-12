package b7.tools.tracking;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Class to provide helpful methods to deal
 * with data from web crawlers for saving
 * / loading data
 */
public class CrawlerDataHandler {

    /**
     * Saves the given SentaiFilmworksCrawler object into the specified filename (.json format suggested)
     * @param crawler the SentaiFilmworksCrawler to save
     * @param filename the file to save to SentaiFilmworsCrawler to
     */
    public static void saveSentaiFilmworksCrawler(SentaiFilmworksCrawler crawler, String filename) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filename);
        try {
            mapper.writeValue(file, crawler);
        }
        catch(JsonMappingException ex) {
            System.err.println("[ERROR] Could not save Sentai Filmworks Crawler to " + filename);
            ex.printStackTrace();
        }
        catch(IOException ex) {
            System.err.println("[ERROR] Could not save Sentai Filmworks Crawler to " + filename);
            ex.printStackTrace();
        }
    }

    /**
     * Loads a SentaiFilmworksCrawler object from the specified filename
     * @param filename the file to load a SentaiFilmworksCrawler from
     * @return the loaded SentaiFilmworksCrawler, or null if there was an issue with loading the file
     */
    public static SentaiFilmworksCrawler loadSentaiFilmworksCrawler(String filename) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filename);
        try {
            return mapper.readValue(file, SentaiFilmworksCrawler.class);
        }
        catch(JsonMappingException ex) {
            System.err.println("[ERROR] Could not load Sentai Filmworks Crawler from " + filename);
            ex.printStackTrace();
        }
        catch(IOException ex) {
            System.err.println("[ERROR] Could not load Sentai Filmworks Crawler from " + filename);
            ex.printStackTrace();
        }
        return null;
    }
}
