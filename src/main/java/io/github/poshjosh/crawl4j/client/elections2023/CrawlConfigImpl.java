package io.github.poshjosh.crawl4j.client.elections2023;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import io.github.poshjosh.crawl4j.client.Timings;

public class CrawlConfigImpl extends CrawlConfig {
    public CrawlConfigImpl(String userAgent) {
        setCrawlStorageFolder(PathUtil.OWN_STORAGE_FOLDER_DATA);
        setIncludeBinaryContentInCrawling(true);
        setIncludeHttpsPages(true);
        setMaxDownloadSize(100_000_000);
        setMaxPagesToFetch(5_000); // Default value
        setPolitenessDelay(Timings.INTERVAL_MILLIS);
        setRespectNoFollow(false);
        setRespectNoIndex(false);
        setResumableCrawling(true);
        //setShutdownOnEmptyQueue(false); // We have our shutdown command
        setUserAgentString(userAgent);
    }
}
