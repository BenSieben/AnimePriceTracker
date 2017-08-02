package b7.tools.tracking;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * Class to run JUnit tests on the
 * b7.tools.tracking.WebCrawler class
 */
public class WebCrawlerTest {

    /**
     * Test to make sure a WebCrawler can successfully connect to
     * a basic internet web page
     */
    @Test
    public void testWebCrawler() {
        final String INITIAL_URL = "https://www.google.com";
        WebCrawler webCrawler = new WebCrawler(INITIAL_URL);
        String initialPageContents = webCrawler.getInitialURLContents();
        assertTrue(initialPageContents.length() > 0);
    }
}
