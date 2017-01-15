package webcrawler.spike;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WebCrawler {

    private static final int RETRIEVE_TIMEOUT = 2 * 1000;

    public static void main(String[] args) throws IOException {
        String baseUrl = "http://www.samoht.com";
        String pageUri = "/resume.html";

        Map<String, Set<String>> siteMap = new HashMap<>();
        buildPageMap(siteMap, baseUrl, pageUri);

        siteMap.entrySet().forEach(pageEntry -> {
            System.out.println(pageEntry.getKey());
            pageEntry.getValue().forEach(link -> System.out.println("    " + link));
        });
    }

    private static void buildPageMap(Map<String, Set<String>> siteMap, String baseUrl, String pageUri) throws IOException {
        if (!siteMap.containsKey(pageUri)) {
            Set<String> linkUris = getLinkUrisForPage(baseUrl, pageUri);
            System.out.println("linkUris = " + linkUris);

            siteMap.put(pageUri, linkUris);
            linkUris.forEach(link -> {
                try {
                    buildPageMap(siteMap, baseUrl, link);
                } catch (IOException ignore) {
                    // Probably a bad URL
                }
            });
        }
    }

    private static Set<String> getLinkUrisForPage(String baseUrl, String pageUri) throws IOException {
        String pageUrl = baseUrl + pageUri;
        System.out.println("Visiting: " + pageUrl);
        URL url = new URL(pageUrl);

        Connection.Response res = Jsoup.connect(pageUrl).timeout(2*1000).execute();
        if ("text/html".equals(res.contentType())) {
            Document doc = Jsoup.parse(url, RETRIEVE_TIMEOUT);
            Elements elements = doc.select("a");
            return elements.stream()
                    .map(it -> it.attr("href"))
                    .filter(it -> it.startsWith("/"))
                    .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }
}
