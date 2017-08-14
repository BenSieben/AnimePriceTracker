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

    @Test
    public void testFormatExcelCSV() {
        String content = "here, is a test\" comment!";
        String expectedResult = "\"here, is a test\"\" comment!\"";
        String result = CrawlerDataHandler.formatForExcelCSV(content);
        assertEquals(expectedResult, result);

        content = "lots of \"\" quotes are in \" this one!";
        expectedResult = "\"lots of \"\"\"\" quotes are in \"\" this one!\"";
        result = CrawlerDataHandler.formatForExcelCSV(content);
        assertEquals(expectedResult, result);
    }
}
