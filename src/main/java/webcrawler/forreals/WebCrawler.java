package webcrawler.forreals;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Recursively crawls the given site, avoiding re-crawling pages already visited.  This provides
 * a natural termination condition for the recursion.
 */
class WebCrawler {
    private PageScraper pageScraper = new PageScraper();

    void crawl(Map<String, Set<String>> siteMap, String baseUrl, String pageUri) {
        String normalizedPageUri = pageUri.replace(baseUrl, "");

        if (!siteMap.containsKey(normalizedPageUri)) {
            System.out.println("Crawling " + normalizedPageUri);

            Set<String> linkUris = getInterestingLinkUris(baseUrl, normalizedPageUri);

            siteMap.put(normalizedPageUri, linkUris);

            linkUris.stream()
                    .filter(link -> isCrawlable(link, baseUrl))
                    .parallel()   // the magic "gofast" bit, but not threadsafe in this implementation -- still cool.
                    .forEach(link -> {
                        crawl(siteMap, baseUrl, link);
                    });
        }
    }

    // Lets not even bother with some classes of link.  I've probably missed some.  Easy to add.
    private Set<String> getInterestingLinkUris(String baseUrl, String normalizedPageUri) {
        return pageScraper.getLinks(baseUrl + normalizedPageUri)
                .stream()
                .filter(link -> !link.isEmpty())
                .filter(link -> !link.startsWith("mailto:"))
                .filter(link -> !link.startsWith("tel:"))
                .filter(link -> !link.startsWith("#"))
                .collect(toSet());
    }

    // Fairly cheesy way to avoid following undesirable links.
    private boolean isCrawlable(String link, String baseUrl) {
        return link.startsWith(baseUrl) || link.startsWith("/");
    }

    void setPageScraper(PageScraper pageScraper) {
        this.pageScraper = pageScraper;
    }
}
