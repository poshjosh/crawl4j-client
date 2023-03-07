package io.github.poshjosh.crawl4j.client;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.exceptions.PageBiggerThanMaxSizeException;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import io.github.poshjosh.crawl4j.client.net.SeleniumHttpClient;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Adapted from edu.uci.ics.crawler4j.fetcher.PageFetcher
 *
 * This adds support for loading HTML defined by scripts.
 */
public class PageFetcherImpl extends PageFetcher {
    private final Predicate<String> fetchJavascript;
    private final SeleniumHttpClient seleniumHttpClient;
    public PageFetcherImpl(CrawlConfig config, CrawlStat crawlStat, Predicate<String> fetchJavascript) {
        super(config);
        this.fetchJavascript = Objects.requireNonNull(fetchJavascript);
        this.seleniumHttpClient = new SeleniumHttpClient(crawlStat);
    }

    public PageFetchResult fetchPage(WebURL webUrl)
            throws InterruptedException, IOException, PageBiggerThanMaxSizeException {
        // Getting URL, setting headers & content
        PageFetchResult fetchResult = new PageFetchResult();
        String toFetchURL = webUrl.getURL();
        HttpUriRequest request = null;
        try {
            if (config.getPolitenessDelay() > 0) {
                // Applying Politeness delay
                synchronized (mutex) {
                    long now = (new Date()).getTime();
                    if ((now - lastFetchTime) < config.getPolitenessDelay()) {
                        Thread.sleep(config.getPolitenessDelay() - (now - lastFetchTime));
                    }
                    lastFetchTime = (new Date()).getTime();
                }
            }

            final CloseableHttpResponse response;
            if (fetchJavascript.test(toFetchURL)) {
                synchronized (mutex) {
                    response = seleniumHttpClient.get(toFetchURL);
                }
            } else {
                request = newHttpUriRequest(toFetchURL);
                response = httpClient.execute(request);
            }

            fetchResult.setEntity(response.getEntity());
            fetchResult.setResponseHeaders(response.getAllHeaders());

            // Setting HttpStatus
            int statusCode = response.getStatusLine().getStatusCode();

            // If Redirect ( 3xx )
            if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY ||
                    statusCode == HttpStatus.SC_MOVED_TEMPORARILY ||
                    statusCode == HttpStatus.SC_MULTIPLE_CHOICES ||
                    statusCode == HttpStatus.SC_SEE_OTHER ||
                    statusCode == HttpStatus.SC_TEMPORARY_REDIRECT ||
                    statusCode == 308) { // todo follow
                // https://issues.apache.org/jira/browse/HTTPCORE-389

                Header header = response.getFirstHeader("Location");
                if (header != null) {
                    String movedToUrl =
                            URLCanonicalizer.getCanonicalURL(header.getValue(), toFetchURL);
                    fetchResult.setMovedToUrl(movedToUrl);
                }
            } else if (statusCode >= 200 && statusCode <= 299) { // is 2XX, everything looks ok
                fetchResult.setFetchedUrl(toFetchURL);
                String uri = request == null ? toFetchURL : request.getURI().toString();
                if (!uri.equals(toFetchURL)) {
                    if (!URLCanonicalizer.getCanonicalURL(uri).equals(toFetchURL)) {
                        fetchResult.setFetchedUrl(uri);
                    }
                }

                // Checking maximum size
                if (fetchResult.getEntity() != null) {
                    long size = fetchResult.getEntity().getContentLength();
                    if (size == -1) {
                        Header length = response.getLastHeader("Content-Length");
                        if (length == null) {
                            length = response.getLastHeader("Content-length");
                        }
                        if (length != null) {
                            size = Integer.parseInt(length.getValue());
                        }
                    }
                    if (size > config.getMaxDownloadSize()) {
                        //fix issue #52 - consume entity
                        response.close();
                        throw new PageBiggerThanMaxSizeException(size);
                    }
                }
            }

            fetchResult.setStatusCode(statusCode);
            return fetchResult;

        } finally { // occurs also with thrown exceptions
            if ((fetchResult.getEntity() == null) && (request != null)) {
                request.abort();
            }
        }
    }

    public synchronized void shutDown() {
        try {
            super.shutDown();
        } finally {
            seleniumHttpClient.close();
        }
    }
}
