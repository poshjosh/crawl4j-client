package io.github.poshjosh.crawl4j.client;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import io.github.poshjosh.crawl4j.client.elections2023.presidential.DownloadLinkTest;
import io.github.poshjosh.crawl4j.client.elections2023.presidential.UrlTest;
import io.github.poshjosh.crawl4j.client.elections2023.PathUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Predicate;

public class CrawlService {

    private final Predicate<String> followUrlTest;

    private final Predicate<String> downloadLinkTest;

    public CrawlService() {
        followUrlTest = new UrlTest();

        downloadLinkTest = new DownloadLinkTest();
    }

    public CrawlController start(String userAgent, Set<String> seedUrls, int numberOfCrawlers) throws Exception{

        CrawlStat crawlStat = new CrawlStat();

        CrawlController crawlController = createController(crawlStat, userAgent, seedUrls);

        CrawlController.WebCrawlerFactory<WebCrawler> factory = () -> new CrawlerImpl(crawlStat, followUrlTest, followUrlTest, downloadLinkTest);

        // Start the crawl.
        // This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        crawlController.start(factory, numberOfCrawlers);

        return crawlController;
    }

    public CrawlController startNonBlocking(String userAgent, Set<String> seedUrls, int numberOfCrawlers)
            throws Exception{

        CrawlStat crawlStat = new CrawlStat();

        CrawlController crawlController = createController(crawlStat, userAgent, seedUrls);

        CrawlController.WebCrawlerFactory<WebCrawler> factory = () -> new CrawlerImpl(crawlStat, followUrlTest, followUrlTest, downloadLinkTest);

        crawlController.startNonBlocking(factory, numberOfCrawlers);

        return crawlController;
    }

    public CrawlController createController(CrawlStat crawlStat, String userAgent, Set<String> seedUrls) throws Exception{
        CrawlConfig config = new CrawlConfig();
        customize(config, userAgent);
        Path path = Paths.get(config.getCrawlStorageFolder());
        PathUtil.createDirIfNotExisting(path);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcherImpl(config, crawlStat, followUrlTest);
        Parser parser = new Parser(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfigImpl(userAgent);
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, parser, robotstxtServer);
        seedUrls.stream().forEach(controller::addSeed);
        return controller;
    }

    private void customize(CrawlConfig crawlConfig, String userAgent) {
        crawlConfig.setCrawlStorageFolder(PathUtil.OWN_STORAGE_FOLDER_DATA);
        crawlConfig.setIncludeBinaryContentInCrawling(true);
        crawlConfig.setIncludeHttpsPages(true);
        crawlConfig.setMaxDownloadSize(100_000_000);
        crawlConfig.setMaxPagesToFetch(5_000); // Default value
        crawlConfig.setPolitenessDelay(Timings.INTERVAL_MILLIS);
        crawlConfig.setRespectNoFollow(false);
        crawlConfig.setRespectNoIndex(false);
        crawlConfig.setResumableCrawling(true);
        //c.setShutdownOnEmptyQueue(false); // We have our shutdown command
        crawlConfig.setUserAgentString(userAgent);
    }
}