package io.github.poshjosh.crawl4j.client;

import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;

public class RobotstxtConfigImpl extends RobotstxtConfig {
    public RobotstxtConfigImpl(String userAgent) {
        setUserAgentName(userAgent);
        setIgnoreUADiscrimination(true);
    }
}
