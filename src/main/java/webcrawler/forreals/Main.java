package webcrawler.forreals;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Map<String, Set<String>> siteMap = new LinkedHashMap<>();
        WebCrawler crawler = new WebCrawler();
        String baseUrl = "http://www.samoht.com";

        crawler.crawl(siteMap, baseUrl, "/");

        print(siteMap, baseUrl);

        long stop = System.currentTimeMillis();
        System.out.println("Runtime: " + (stop - start) / 1000L + "s");
    }

    private static void print(Map<String, Set<String>> siteMap, String baseUrl) {
        System.out.println("\n-------- Site Map for + " + baseUrl + " --------");
        siteMap.entrySet().forEach(pageEntry -> {
            System.out.println(pageEntry.getKey());
            pageEntry.getValue().forEach(link -> System.out.println("    " + link));
        });
    }
}
