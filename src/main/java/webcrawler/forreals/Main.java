package webcrawler.forreals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Oops!  Please provide the URL from which to start.");
            return;
        }
        long start = System.currentTimeMillis();

        Map<String, Set<String>> siteMap = new LinkedHashMap<>();
        WebCrawler crawler = new WebCrawler();

        String[] parts = parseUrl(args[0]);
        String baseUrl = parts[0];
        String path = parts[1];
        crawler.crawl(siteMap, baseUrl, path);

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

    private static String[] parseUrl(String urlArg) {
        String baseUrl = null;
        String startPath = null;
        int port;
        try {
            URL url = new URL(urlArg);
            port = url.getPort();
            String portString = url.getPort() == -1 ? "" : ":" + port;
            baseUrl = url.getProtocol() + "://" + url.getHost() + portString;
            startPath = url.getPath().isEmpty() ? "/" : url.getPath();
            System.out.println("*** Starting at " + urlArg);
        } catch (MalformedURLException e) {
            System.out.println("That doesn't appear to be a valid URL.");
        }
        return new String[]{baseUrl, startPath};
    }
}
