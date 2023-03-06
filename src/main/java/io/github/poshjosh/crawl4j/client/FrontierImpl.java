package io.github.poshjosh.crawl4j.client;

import com.sleepycat.je.Environment;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.frontier.Counters;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.HashSet;
import java.util.Set;

public class FrontierImpl extends Frontier {

    private Set<WebURL> removedPages = new HashSet<>(100);
    public FrontierImpl(Environment env, CrawlConfig config) {
        super(env, config);
    }

    @Override
    public void setProcessed(WebURL webURL) {
        counters.increment(Counters.ReservedCounterNames.PROCESSED_PAGES);
        if (inProcessPages != null) {
            if (!inProcessPages.removeURL(webURL)) {
                if (removedPages.contains(webURL)) {
                    logger.warn("Already removedPages: {} from list of processed pages.", webURL.getURL());
                } else {
                    logger.warn("Could not remove: {} from list of processed pages.", webURL.getURL());
                }
            } else {
                removedPages.add(webURL);
                if (removedPages.size() == 10_000) {
                    removedPages.clear();
                }
            }
        }
    }
}
