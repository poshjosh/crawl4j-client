package io.github.poshjosh.crawl4j.client;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlControllerImpl extends CrawlController {
    public CrawlControllerImpl(CrawlConfig config, PageFetcher pageFetcher, RobotstxtServer robotstxtServer) throws Exception {
        super(config, pageFetcher, robotstxtServer);
        frontier = new FrontierImpl(env, config);
    }

    public CrawlControllerImpl(CrawlConfig config, PageFetcher pageFetcher, Parser parser, RobotstxtServer robotstxtServer) throws Exception {
        super(config, pageFetcher, parser, robotstxtServer);
        frontier = new FrontierImpl(env, config);
    }
}
