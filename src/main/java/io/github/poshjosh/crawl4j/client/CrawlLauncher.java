package io.github.poshjosh.crawl4j.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlLauncher {

    private static final Logger logger = LoggerFactory.getLogger(CrawlLauncher.class);

    //public static final int [] stateIds = {5, 7, 8, 13, 16, 18, 22, 24, 29, 30, 33, 34}; //, 36, 37
    public static final int [] stateIds = {33};

    public static void main(String[] args) throws Exception {
        new CrawlService().start(stateIds);
    }
}
