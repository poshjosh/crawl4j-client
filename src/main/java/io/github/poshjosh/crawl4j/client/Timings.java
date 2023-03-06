package io.github.poshjosh.crawl4j.client;

import java.util.concurrent.TimeUnit;

public final class Timings {
    private Timings() { }
    public static final int PAGELOAD_TIMEOUT_MILLIS = 30_000;
    public static final int INTERVAL_MILLIS = (CrawlLauncher.stateIds.length / 2) * 1000;
    public static final long TIMEOUT_MILLIS = TimeUnit.MINUTES.toMillis(60);
}
