package b7.tools.tracking;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Class to run JUnit tests on the
 * b7.tools.tracking.CrawlerDataHandler
 */
public class CrawlerDataHandlerTest {

    @Test
    public void testSaveLoadSentaiFilmworks() {
        SentaiFilmworksCrawler sentaiFilmworksCrawler = new SentaiFilmworksCrawler();
        CrawlerDataHandler.saveSentaiFilmworksCrawler(sentaiFilmworksCrawler, AnimeCrawlerController.SENTAI_FILMWORKS_CRAWLER_FILENAME);

        SentaiFilmworksCrawler loadedSentaiFilmworksCrawler = CrawlerDataHandler.loadSentaiFilmworksCrawler(AnimeCrawlerController.SENTAI_FILMWORKS_CRAWLER_FILENAME);
        assertNotNull(loadedSentaiFilmworksCrawler);
    }

    @Test
    public void testSaveLoadCrawlData() {
        String filename = "savedata/crawlers/testcrawldata.json";
        CrawlData webCrawler = new CrawlData("Test Crawl Data");
        CrawlerDataHandler.saveCrawlData(webCrawler, filename);

        CrawlData loadedCrawlData = CrawlerDataHandler.loadCrawlData(filename);
        assertNotNull(loadedCrawlData);
    }
}
