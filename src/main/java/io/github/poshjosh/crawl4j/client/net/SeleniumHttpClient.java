package io.github.poshjosh.crawl4j.client.net;

import io.github.poshjosh.crawl4j.client.CrawlStat;
import io.github.poshjosh.crawl4j.client.StringUtil;
import io.github.poshjosh.crawl4j.client.Timings;
import io.github.poshjosh.crawl4j.client.elections2023.presidential.ElectionResultDownloader;
import io.github.poshjosh.crawl4j.client.elections2023.presidential.ElementClickableConditions;
import io.github.poshjosh.crawl4j.client.elections2023.presidential.Urls;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SeleniumHttpClient implements AutoCloseable{

    private static final Logger logger = LoggerFactory.getLogger(SeleniumHttpClient.class);

    private final WebDriver webDriver;
    private final ExpectedCondition<List<WebElement>> pageClickableCondition;

    private final ElectionResultDownloader downloader;

    public SeleniumHttpClient(CrawlStat crawlStat) {
        this.webDriver = new ChromeDriver();
        this.webDriver.manage().timeouts().implicitlyWait(Duration.ofMillis(Timings.PAGELOAD_TIMEOUT_MILLIS));
        this.pageClickableCondition = new ElementClickableConditions();
        this.downloader = new ElectionResultDownloader(webDriver, crawlStat);
    }

    public CloseableHttpResponse get(String url) throws UnsupportedEncodingException {

        webDriver.get(url);

        List<WebElement> elements = pageClickableCondition.apply(webDriver);

        String responseString = webDriver.getPageSource();

        Optional<String> errorOptional = getError(responseString);

        final CloseableHttpResponse response;
        if (errorOptional.isPresent()) {
            String error = errorOptional.get();
            logger.warn("{} for {}", error, url);
            int code = error.contains("Denied") ? 403 : 409;
            response = new CloseableHttpResponseImpl(HttpVersion.HTTP_1_1, code, getMessage(error).orElse("Error"));
        } else {
            response = new CloseableHttpResponseImpl(HttpVersion.HTTP_1_1, 200, "OK");
        }
        response.setEntity(new StringEntity(responseString, ContentType.TEXT_HTML.getMimeType(), StandardCharsets.UTF_8.name()));
        response.setHeader("Content-Length", ""+responseString.length());
        response.setLocale(Locale.ENGLISH);

        if (Urls.UrlType.PollingUnit.equals(Urls.getType(url))) {
            if (elements.isEmpty()) {
                elements = pageClickableCondition.apply(webDriver);
            }
            downloader.download(elements);
        }

        return response;
    }

    private Optional<String> getError(String s) {
        return StringUtil.get(s, "<Error>", "</Error>");
    }
    private Optional<String> getMessage(String s) {
        return StringUtil.get(s, "<Message>", "</Message>");
    }

    @Override
    public void close() {
        webDriver.quit();
    }
}
