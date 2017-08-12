package b7.tools.tracking;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;

/**
 * Class to run JUnit tests on the
 * b7.tools.tracking.CrawlData
 */
public class CrawlDataTest {

    @Test
    public void testTitle() {
        CrawlData crawlData = new CrawlData("");
        assertEquals(CrawlData.DEFAULT_TITLE, crawlData.getTitle());

        crawlData.setTitle("Crawler 3");
        assertEquals("Crawler 3", crawlData.getTitle());
    }
}
