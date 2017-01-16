package webcrawler.forreals;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class WebCrawlerTest {

    private WebCrawler crawler;
    private PageScraper pageScraper;
    private Map<String, Set<String>> siteMap;

    @Before
    public void setUp() throws Exception {
        crawler = new WebCrawler();

        pageScraper = mock(PageScraper.class);
        crawler.setPageScraper(pageScraper);
        siteMap = new HashMap<>();
    }

    @Test
    public void crawlNoLinks() {
        when(pageScraper.getLinks("baseUrl/startingUri"))
                .thenReturn(emptySet());

        crawler.crawl(siteMap, "baseUrl", "/startingUri");

        assertEquals(emptySet(), siteMap.get("/startingUri"));
    }

    @Test
    public void crawlTopPage() {
        when(pageScraper.getLinks("baseUrl/startingUri"))
                .thenReturn(singleton("/subUri"));
        when(pageScraper.getLinks("baseUrl/subUri"))
                .thenReturn(emptySet());

        crawler.crawl(siteMap, "baseUrl", "/startingUri");

        assertEquals(2, siteMap.size());
        assertEquals(singleton("/subUri"), siteMap.get("/startingUri"));
        assertEquals(emptySet(), siteMap.get("/subUri"));
    }

    @Test
    public void normalizeAbsoluteUrlLinks() {
        when(pageScraper.getLinks("baseUrl/startingUri"))
                .thenReturn(emptySet());

        crawler.crawl(siteMap, "baseUrl", "baseUrl/startingUri"); // It happens (a LOT on the Wipro page)

        assertEquals(1, siteMap.size());
        assertEquals(emptySet(), siteMap.get("/startingUri"));
    }

    @Test
    public void filterOutMailtoLinks() {
        when(pageScraper.getLinks("baseUrl/startingUri"))
                .thenReturn(singleton("mailto:somewhere"));

        crawler.crawl(siteMap, "baseUrl", "/startingUri");

        assertEquals(1, siteMap.size());
        assertEquals(emptySet(), siteMap.get("/startingUri"));
    }

    @Test
    public void filterOutEmptyLinks() {
        when(pageScraper.getLinks("baseUrl/startingUri"))
                .thenReturn(singleton(""));

        crawler.crawl(siteMap, "baseUrl", "/startingUri");

        assertEquals(1, siteMap.size());
        assertEquals(emptySet(), siteMap.get("/startingUri"));
    }

    @Test
    public void filterOutTelLinks() {
        when(pageScraper.getLinks("baseUrl/startingUri"))
                .thenReturn(singleton("tel:notgonnadoit"));

        crawler.crawl(siteMap, "baseUrl", "/startingUri");

        assertEquals(1, siteMap.size());
        assertEquals(emptySet(), siteMap.get("/startingUri"));
    }

    @Test
    public void filterOutAnchorLinks() {
        when(pageScraper.getLinks("baseUrl/startingUri"))
                .thenReturn(singleton("#nope"));

        crawler.crawl(siteMap, "baseUrl", "/startingUri");

        assertEquals(1, siteMap.size());
        assertEquals(emptySet(), siteMap.get("/startingUri"));
    }

    @Test
    public void crawlRecursive() {
        when(pageScraper.getLinks("baseUrl/startingUri"))
                .thenReturn(singleton("/subUri"));
        when(pageScraper.getLinks("baseUrl/subUri"))
                .thenReturn(singleton("/subSubUri"));
        when(pageScraper.getLinks("baseUrl/subSubUri"))
                .thenReturn(singleton("/subSubSubUri"));
        when(pageScraper.getLinks("baseUrl/subSubSubUri"))
                .thenReturn(emptySet());

        crawler.crawl(siteMap, "baseUrl", "/startingUri");

        assertEquals(4, siteMap.size());
        assertEquals(singleton("/subUri"), siteMap.get("/startingUri"));
        assertEquals(singleton("/subSubUri"), siteMap.get("/subUri"));
        assertEquals(singleton("/subSubSubUri"), siteMap.get("/subSubUri"));
        assertEquals(emptySet(), siteMap.get("/subSubSubUri"));
    }

    @Test
    public void crawlRecursiveBacklinkTerminatesProperly() {
        when(pageScraper.getLinks("baseUrl/startingUri"))
                .thenReturn(singleton("/subUri"));

        HashSet<String> subUriLinks = new HashSet<>(asList("/subSubUri", "/startingUri"));
        when(pageScraper.getLinks("baseUrl/subUri"))
                .thenReturn(subUriLinks);

        when(pageScraper.getLinks("baseUrl/subSubUri"))
                .thenReturn(new HashSet<>(emptySet()));

        crawler.crawl(siteMap, "baseUrl", "/startingUri");

        assertEquals(3, siteMap.size());
        assertEquals(singleton("/subUri"), siteMap.get("/startingUri"));
        assertEquals(subUriLinks, siteMap.get("/subUri"));
        assertEquals(emptySet(), siteMap.get("/subSubUri"));
    }

    @Test
    public void crawlDontFollowOutsideLinks() {
        when(pageScraper.getLinks("baseUrl/startingUri"))
                .thenReturn(singleton("http://someotherdomain.com"));

        crawler.crawl(siteMap, "baseUrl", "/startingUri");

        verify(pageScraper, times(0)).getLinks("baseUrlhttp://someotherdomain.com");
        assertEquals(1, siteMap.size());
        assertEquals(singleton("http://someotherdomain.com"), siteMap.get("/startingUri"));
    }
}