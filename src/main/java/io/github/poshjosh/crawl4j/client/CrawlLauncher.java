package io.github.poshjosh.crawl4j.client;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import io.github.poshjosh.crawl4j.client.elections2023.presidential.Urls;
import io.github.poshjosh.crawl4j.client.net.UserAgents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CrawlLauncher {

    private static final Logger logger = LoggerFactory.getLogger(CrawlLauncher.class);

    //public static final int [] stateIds = {5, 7, 8, 13, 16, 18, 22, 24, 29, 30, 33, 34}; //, 36, 37
    public static final int [] stateIds = {33};


    public static void main(String[] args) throws Exception {

        CrawlService crawlService = new CrawlService();

        UserAgents userAgents = new UserAgents();

        List<CrawlController> controllers = new ArrayList<>(stateIds.length);
        for (int i = 0; i < stateIds.length; i++) {
            String seedUrl = Urls.BASE + Urls.PRESIDENTIAL_ID + "?state=" + stateIds[i];
            Set<String> seedUrls = new HashSet<>();
            seedUrls.add(seedUrl);
            logger.info("Starting crawler[{}] with seed urls: {}", i, seedUrls);
            controllers.add(crawlService.startNonBlocking(userAgents.any(seedUrl, false), seedUrls, 1));
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
        int page = 0;
        int links = 0;
        int filesDownloaded = 0;
        int bytesDownloaded = 0;
        for (Object localData : crawlersLocalData) {
            CrawlStat stat = (CrawlStat) localData;
            page += stat.getTotalProcessedPages();
            links += stat.getTotalLinks();
            filesDownloaded += stat.getTotalFilesDownloaded();
            bytesDownloaded += stat.getTotalBytesDownloaded();
        }
        logger.info("Aggregated Statistics:\nPages: {}, links: {}, downloaded: {}, bytes: {}, average download size: {}",
                page, links, filesDownloaded, bytesDownloaded,
                filesDownloaded == 0 ? 0 : (bytesDownloaded / filesDownloaded));
    }
}
