package io.github.poshjosh.crawl4j.client;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import io.github.poshjosh.crawl4j.client.elections2023.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Predicate;

public class CrawlService {

    private final Predicate<String> followUrlTest;

    private final Predicate<String> downloadLinkTest;

    public CrawlService() {
        followUrlTest = new PresidentialResultFollowUrlTest();

        downloadLinkTest = new DownloadLinkTest();
    }

    public CrawlController start(String userAgent, Set<String> seedUrls, int numberOfCrawlers) throws Exception{

        CrawlController crawlController = createController(userAgent);

        seedUrls.stream().forEach(crawlController::addSeed);

        CrawlController.WebCrawlerFactory<WebCrawler> factory = () -> new CrawlerImpl(followUrlTest, followUrlTest, downloadLinkTest);

        // Start the crawl.
        // This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        crawlController.start(factory, numberOfCrawlers);

        return crawlController;
    }

    public CrawlController startNonBlocking(String userAgent, Set<String> seedUrls, int numberOfCrawlers, long timeoutMillis)
            throws Exception {

        CrawlController crawlController = startNonBlocking(userAgent, seedUrls, numberOfCrawlers);

        Thread.sleep(timeoutMillis);

        // Send the shutdown request and then wait for finishing
        crawlController.shutdown();
        crawlController.waitUntilFinish();

        return crawlController;
    }

    public CrawlController startNonBlocking(String userAgent, Set<String> seedUrls, int numberOfCrawlers)
            throws Exception{

        CrawlController crawlController = createController(userAgent);

        seedUrls.stream().forEach(crawlController::addSeed);

        CrawlController.WebCrawlerFactory<WebCrawler> factory = () -> new CrawlerImpl(followUrlTest, followUrlTest, downloadLinkTest);

        crawlController.startNonBlocking(factory, numberOfCrawlers);

        return crawlController;
    }

    public CrawlController createController(String userAgent) throws Exception{
        CrawlConfig config = new CrawlConfigImpl(userAgent);
        Path path = Paths.get(config.getCrawlStorageFolder());
        PathUtil.createDirIfNotExisting(path);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcherImpl(config, followUrlTest);
        Parser parser = new ParserImpl(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfigImpl(userAgent);
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        return new CrawlControllerImpl(config, pageFetcher, parser, robotstxtServer);
    }
}