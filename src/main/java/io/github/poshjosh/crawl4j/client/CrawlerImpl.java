package io.github.poshjosh.crawl4j.client;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import io.github.poshjosh.crawl4j.client.elections2023.PathUtil;
import java.util.Objects;
import java.util.function.Predicate;

public class CrawlerImpl extends WebCrawler {

    private final CrawlStat crawlStat;

    private final Predicate<String> followUrlTest;
    private final Predicate<String> visitUrlTest;
    private final Predicate<String> downloadUrlTest;

    public CrawlerImpl(CrawlStat crawlStat, Predicate<String> followUrlTest, Predicate<String> visitUrlTest, Predicate<String> downloadUrlTest) {
        this.followUrlTest = Objects.requireNonNull(followUrlTest);
        this.visitUrlTest = Objects.requireNonNull(visitUrlTest);
        this.downloadUrlTest = Objects.requireNonNull(downloadUrlTest);
        this.crawlStat = Objects.requireNonNull(crawlStat);
    }

    @Override
    protected boolean shouldFollowLinksIn(WebURL url) {
        final boolean shouldFollowLinksIn = followUrlTest.test(getHref(url));
        logger.trace("Should follow links in: {}, {}", shouldFollowLinksIn, url);
        return shouldFollowLinksIn;
    }

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        final boolean shouldVisit = visitUrlTest.test(getHref(url));
        logger.trace("Should visit: {}, {}", shouldVisit, url);
        return shouldVisit;
    }

    private String getHref(WebURL url) { return url.getURL(); }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {

        String url = page.getWebURL().getURL();
        crawlStat.setLastVisitUrl(url);

        crawlStat.incTotalProcessedPages(1);

        ParseData parseData = page.getParseData();
        if (parseData instanceof HtmlParseData) {
            crawlStat.incTotalLinks(parseData.getOutgoingUrls().size());
            //logger.info("\nExtracted urls: {}", parseData.getOutgoingUrls().size());
            //logger.info("\nExtracted html: {}", ((HtmlParseData) parseData).getHtml());
        }

        // We dump this crawler statistics after processing every 50 pages
        if ((crawlStat.getTotalProcessedPages() % 50) == 0) {
            dumpCrawlStat();
        }

        if (!(parseData instanceof BinaryParseData)) {
            return;
        }

        if (!downloadUrlTest.test(url)) {
            return;
        }

        byte [] bytesToSave = page.getContentData();
        if (PathUtil.save(url, bytesToSave).isPresent()) {
            crawlStat.recordSavedBytes(bytesToSave);
        }
    }


    /**
     * This function is called by controller to get the local data of this crawler when job is
     * finished
     */
    @Override
    public Object getMyLocalData() {
        return crawlStat;
    }

    /**
     * This function is called by controller before finishing the job.
     * You can put whatever stuff you need here.
     */
    @Override
    public void onBeforeExit() {
        dumpCrawlStat();
    }

    public void dumpCrawlStat() {
        logger.info("{}\t{}", getMyId(), crawlStat);
    }
}