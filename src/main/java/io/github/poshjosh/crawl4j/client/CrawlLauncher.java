package io.github.poshjosh.crawl4j.client;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import io.github.poshjosh.crawl4j.client.elections2023.Urls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CrawlLauncher {

    private static final Logger logger = LoggerFactory.getLogger(CrawlLauncher.class);

    // We use 12 of these because there are 12 processors on my local machine
    public static final String [] userAgents = {
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.A.B.C Safari/525.13",
            "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US) AppleWebKit/525.19 (KHTML, like Gecko) Chrome/0.4.154.29 Safari/525.19",
            "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_6; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.133 Safari/534.16",
            "Mozilla/5.0 (X11; U; Linux i686; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Ubuntu/10.10 Chromium/10.0.648.133 Chrome/10.0.648.133 Safari/534.16",
            "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_6; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.151 Safari/534.16",
            "Mozilla/5.0 (X11; U; Linux i686 (x86_64); en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.634.0 Safari/534.16",
            "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.04 (lucid) Firefox/3.6.13 GTB5",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; de; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8",
            "Mozilla/5.0 (Windows; U; Windows NT 6.0; fr; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8 ( .NET CLR 3.5.30729; .NET4.0C)",
            "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; de-DE; rv:1.9.2.9) Gecko/20100824 Firefox/3.6.9 (.NET CLR 3.5.30729)",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.13) Gecko/20101203 BTRS32096 Firefox/3.6.13"
    };

    public static final int [] stateIds = {5, 7, 8, 13, 16, 18, 22, 24, 29, 30, 33, 34}; //, 36, 37

    public static void main(String[] args) throws Exception {

        CrawlService crawlService = new CrawlService();

        List<CrawlController> controllers = new ArrayList<>(stateIds.length);
        for (int i = 0; i < stateIds.length; i++) {
            Set<String> seedUrls = new HashSet<>();
            seedUrls.add(Urls.BASE + Urls.PRESIDENTIAL_ID + "?state=" + stateIds[i]);
            logger.info("Starting crawler[{}] with seed urls: {}", i, seedUrls);
            controllers.add(crawlService.startNonBlocking(userAgents[i], seedUrls, 1));
        }

        Thread.sleep(Timings.TIMEOUT_MILLIS);

        logger.info("Shutting down {} crawler controllers", controllers.size());
        for (CrawlController controller : controllers) {
            controller.shutdown();
        }

        logger.info("Waiting until finish");
        for (CrawlController controller : controllers) {
            onCompleted(controller.getCrawlersLocalData());
            controller.waitUntilFinish();
        }
    }


    private static void onCompleted(List<Object> crawlersLocalData) {
        int totalProcessedPages = 0;
        int totalFilesDownloaded = 0;
        for (Object localData : crawlersLocalData) {
            CrawlStat stat = (CrawlStat) localData;
            totalProcessedPages += stat.getTotalProcessedPages();
            totalFilesDownloaded += stat.getTotalFilesDownloaded();
        }
        logger.info("Aggregated Statistics:");
        logger.info("\tTotal processed pages: {}", totalProcessedPages);
        logger.info("\tTotal files downloaded: {}", totalFilesDownloaded);
    }
}
