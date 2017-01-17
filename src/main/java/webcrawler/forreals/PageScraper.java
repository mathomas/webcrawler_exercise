package webcrawler.forreals;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;

/**
 * Simple wrapper around Jsoup library to minimize the amount of code that
 * has to be integration tested.  Could have introduced PowerMock or the like
 * in order to mock out static method calls found here, but decided to just take the
 * integration test path for this exercise.
 * <p>
 * Realistically, at some point you really do have to see how the networking library
 * you might be using actually works, and ensure it continues to, so such a test is
 * not totally crazy. The downside is having to point to some stable site.  Would
 * be nice to have a combination of this integration test and a unit test with
 * appropriate mocking.
 */
class PageScraper {

    private static final int RETRIEVE_TIMEOUT = 3 * 1000;

    /**
     * Obtains the appropriate resource reference from anchor and img tags in the passed URL's content.
     *
     * @param pageUrl The fully-qualified url of the page to scrape.
     * @return A set of URIs representing the linked resources.
     */
    Set<ResourceReference> getLinks(String pageUrl) {
        Document doc;
        try {
            doc = Jsoup.parse(new URL(pageUrl), RETRIEVE_TIMEOUT);
        } catch (IOException e) {
            // Cheesy?  Yes!  Could check the URL up front, and also check that the mime
            // type is something we can actually scrape.  If the requirements said to
            // report invalid or dead links and/or otherwise un-crawl-able files, then
            // something else would be better, for sure.
            return emptySet();
        }

        Elements anchors = doc.select("a");
        Set<ResourceReference> anchorHrefs = anchors.stream()
                .map(it -> ResourceReference.page(it.attr("href")))
                .collect(Collectors.toSet());

        Elements imgs = doc.select("img");
        Set<ResourceReference> imgSrcs = imgs.stream()
                .map(it -> ResourceReference.image(it.attr("src")))
                .collect(Collectors.toSet());

        Set<ResourceReference> all = new HashSet<>();
        all.addAll(anchorHrefs);
        all.addAll(imgSrcs);
        return all;
    }
}
