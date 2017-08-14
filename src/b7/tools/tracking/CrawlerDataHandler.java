package b7.tools.tracking;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
        // Try to infer a path of folders that we might have to make from the filename
        int forwardSlashLastIndex = filename.lastIndexOf("/");
        File path = new File(filename.substring(0, forwardSlashLastIndex));
        File file = new File(filename);
        try {
            if(!path.exists()) {  // Create path directories if they do not exist
                path.mkdirs();
            }
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
            System.err.println("[ERROR] JsonMappingException Could not load Sentai Filmworks Crawler from " + filename);
            ex.printStackTrace();
        }
        catch(IOException ex) {
            System.err.println("[ERROR] IOException Could not load Sentai Filmworks Crawler from " + filename);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Saves the given CrawlData object into the specified filename (.json format suggested)
     * @param crawler the CrawlData to save
     * @param filename the file to save to CrawlData to
     */
    public static void saveCrawlData(CrawlData crawler, String filename) {
        ObjectMapper mapper = new ObjectMapper();
        // Try to infer a path of folders that we might have to make from the filename
        int forwardSlashLastIndex = filename.lastIndexOf("/");
        File path = new File(filename.substring(0, forwardSlashLastIndex));
        File file = new File(filename);
        try {
            if(!path.exists()) {  // Create path directories if they do not exist
                path.mkdirs();
            }
            mapper.writeValue(file, crawler);
        }
        catch(JsonMappingException ex) {
            System.err.println("[ERROR] Could not save Crawl Data to " + filename);
            ex.printStackTrace();
        }
        catch(IOException ex) {
            System.err.println("[ERROR] Could not save Crawl Data to " + filename);
            ex.printStackTrace();
        }
    }

    /**
     * Loads a CrawlData object from the specified filename
     * @param filename the file to load a CrawlData from
     * @return the loaded CrawlData, or null if there was an issue with loading the file
     */
    public static CrawlData loadCrawlData(String filename) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filename);
        try {
            return mapper.readValue(file, CrawlData.class);
        }
        catch(JsonMappingException ex) {
            System.err.println("[ERROR] JsonMappingException Could not load Crawl Data from " + filename);
            ex.printStackTrace();
        }
        catch(IOException ex) {
            System.err.println("[ERROR] IOException Could not load Crawl Data from " + filename);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Takes the given crawl data, and saves it in a CSV format to the specified filename
     * @param crawlData the crawl data to generate a CSV for
     * @param filename the filename to use to save the CSV data to
     */
    public static void saveCrawlDataToCSV(CrawlData crawlData, String filename) {
        // Try to infer a path of folders that we might have to make from the filename
        int forwardSlashLastIndex = filename.lastIndexOf("/");
        File path = new File(filename.substring(0, forwardSlashLastIndex));
        try {
            if(!path.exists()) {  // Create path directories if they do not exist
                path.mkdirs();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));

            // Add a header line to describe the columns of the CSV data
            String headerLine = "Name,Current Price,Lowest Price,\n";
            bufferedWriter.write(headerLine);

            // TODO go through the crawl data, convert the data to CSV-friendly format, and write results to the file

            bufferedWriter.close();
        }
        catch(JsonMappingException ex) {
            System.err.println("[ERROR] Could not save CSV version of Crawl Data to " + filename);
            ex.printStackTrace();
        }
        catch(IOException ex) {
            System.err.println("[ERROR] Could not save CSV version of Crawl Data to " + filename);
            ex.printStackTrace();
        }
    }
}
