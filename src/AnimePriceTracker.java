import b7.tools.tracking.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Class with main method to run the program to get price information
 * for anime from online stores
 */
public class AnimePriceTracker {

    public static void main(String[] args) {
        parseBaseSentaiFilmworksPage();
    }

    /**
     * Sets up code to call methods to parse the base Sentai Filmworks store page
     */
    private static void parseBaseSentaiFilmworksPage() {
        SentaiFilmworksCrawler sentaiFilmworksCrawler = new SentaiFilmworksCrawler();
        sentaiFilmworksCrawler.saveBasePage(false);
        sentaiFilmworksCrawler.parseBasePage();
    }
}
