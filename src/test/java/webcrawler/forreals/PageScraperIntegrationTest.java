package webcrawler.forreals;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static webcrawler.forreals.ResourceReference.*;

public class PageScraperIntegrationTest {

    private PageScraper scraper;

    @Before
    public void setUp() throws Exception {
        scraper = new PageScraper();
    }

    @Test
    public void getLinks() throws Exception {
        Set<ResourceReference> linkHrefs = scraper.getLinks("http://www.samoht.com");
        assertEquals(12, linkHrefs.size());
        assertTrue(linkHrefs.contains(page("/resume.html")));
    }

    @Test
    public void getLinks404() throws Exception {
        Set<ResourceReference> linkHrefs = scraper.getLinks("http://www.samoht.com/notthere.html");
        assertTrue(linkHrefs.isEmpty());
    }

    @Test
    public void getLinksNoFileUrls() throws Exception {
        Set<ResourceReference> linkHrefs = scraper.getLinks("file://foo.bar.baz/");
        assertTrue(linkHrefs.isEmpty());
    }

    @Test
    public void getLinksRidiculousUrl() throws Exception {
        Set<ResourceReference> linkHrefs = scraper.getLinks("this will never work");
        assertTrue(linkHrefs.isEmpty());
    }
}