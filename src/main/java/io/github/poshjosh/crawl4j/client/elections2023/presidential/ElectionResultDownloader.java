package io.github.poshjosh.crawl4j.client.elections2023.presidential;

import io.github.poshjosh.crawl4j.client.CrawlStat;
import io.github.poshjosh.crawl4j.client.elections2023.PathUtil;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class ElectionResultDownloader {
    private static final Logger logger = LoggerFactory.getLogger(ElectionResultDownloader.class);
    private final WebDriver webDriver;
    private final CrawlStat crawlStat;
    public ElectionResultDownloader(WebDriver webDriver, CrawlStat crawlStat) {
        this.webDriver = Objects.requireNonNull(webDriver);
        this.crawlStat = Objects.requireNonNull(crawlStat);
    }

    public void download(List<WebElement> elements) {
        // To view the result we have to click a button
        for (WebElement element : elements) {
            if (!click(element)) { // A new page will be loaded
                continue;
            }
            crawlStat.incTotalProcessedPages(1);
            String src;
            try {
                // <iframe _ngcontent-gmg-c54="" width="100%" height="700px" src="https://inec-cvr-cache.s3.eu-west-1.amazonaws.com/cached/results/623555/result_170497_1678037717_thumb.jpg"></iframe>
                WebElement resultFrame = webDriver.findElement(By.tagName("iframe"));
                src = resultFrame.getAttribute("src");
                if (src == null || src.isEmpty()) {
                    return;
                }
            } catch(RuntimeException e) {
                logger.warn("{}", e.toString());
                return;
            }
            crawlStat.incTotalLinks(1);
            try (InputStream in = new URL(src).openStream()) {
                byte [] bytesToSave = toByteArray(in);
                if (PathUtil.save(src, bytesToSave).isPresent()) {
                    crawlStat.recordSavedBytes(bytesToSave);
                }
            } catch(IOException e) {
                logger.warn("{}", e.toString());
            }
        }
    }

    private byte [] toByteArray(InputStream in) throws IOException{
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024 * 16];
        while ((nRead = in.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    private boolean click(WebElement element) {
        try {
            element.click();
            return true;
        } catch (StaleElementReferenceException | NoSuchElementException | ElementClickInterceptedException e) {
            logger.debug("{}", e.toString());
        }
        return false;
    }
}
