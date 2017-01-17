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

            Set<ResourceReference> reportableResourceReferences = getReportableResourceReferences(baseUrl, normalizedPageUri);

            siteMap.put(normalizedPageUri,
                    reportableResourceReferences.stream()
                            .map(ResourceReference::getUri)
                            .collect(toSet()));

            // We only crawl a subset of reportable references:  you report an image, but you don't crawl it.
            reportableResourceReferences.stream()
                    .filter(resource -> isCrawlable(resource, baseUrl))
//                    .parallel()   // the magic "gofast" bit, but not threadsafe in this implementation -- still cool.
                    .forEach(resource -> {
                        crawl(siteMap, baseUrl, resource.getUri());
                    });
        }
    }

    // Lets not even bother with some classes of link.  I've probably missed some.  Easy to add.
    private Set<ResourceReference> getReportableResourceReferences(String baseUrl, String normalizedPageUri) {
        return pageScraper.getLinks(baseUrl + normalizedPageUri)
                .stream()
                .filter(ResourceReference::isReportable)
                .collect(toSet());
    }

    // Fairly cheesy way to avoid following undesirable links.
    private boolean isCrawlable(ResourceReference resourceReference, String baseUrl) {
        return resourceReference.isPage() &&
                resourceReference.isLocal(baseUrl);
    }

    void setPageScraper(PageScraper pageScraper) {
        this.pageScraper = pageScraper;
    }
}
