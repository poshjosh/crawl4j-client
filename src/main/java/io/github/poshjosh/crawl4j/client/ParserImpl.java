package io.github.poshjosh.crawl4j.client;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.exceptions.ParseException;
import edu.uci.ics.crawler4j.parser.*;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.Net;
import edu.uci.ics.crawler4j.util.Util;
import io.github.poshjosh.crawl4j.client.elections2023.DownloadLinkTest;
import io.github.poshjosh.crawl4j.client.elections2023.Urls;
import org.apache.tika.language.LanguageIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Adapted from edu.uci.ics.crawler4j.parser.Parser
 *
 * This implementation uses Jsoup to extract links
 */
public class ParserImpl extends Parser{

    private static final Logger logger = LoggerFactory.getLogger(ParserImpl.class);

    private final CrawlConfig config;

    private final HtmlParser htmlContentParser;

    public ParserImpl(CrawlConfig config) throws IllegalAccessException, InstantiationException {
        super(config);
        this.config = config;
        this.htmlContentParser = new TikaHtmlParser(config);
    }

    public ParserImpl(CrawlConfig config, HtmlParser htmlParser) {
        super(config, htmlParser);
        this.config = config;
        this.htmlContentParser = htmlParser;
    }

    @Override
    public void parse(Page page, String contextURL)
            throws NotAllowedContentException, ParseException {
        String url = page.getWebURL().getURL();
        if (Util.hasBinaryContent(page.getContentType())) { // BINARY
            BinaryParseData parseData = new BinaryParseData();
            if (config.isIncludeBinaryContentInCrawling()) {
                if (config.isProcessBinaryContentInCrawling()) {
                    parseData.setBinaryContent(page.getContentData());
                } else {
                    parseData.setHtml("<html></html>");
                }
                page.setParseData(parseData);
                if (parseData.getHtml() == null) {
                    throw new ParseException();
                }
                parseData.setOutgoingUrls(extractUrls(url, parseData.getHtml()));
            } else {
                throw new NotAllowedContentException();
            }
        } else if (Util.hasPlainTextContent(page.getContentType())) { // plain Text
            try {
                TextParseData parseData = new TextParseData();
                if (page.getContentCharset() == null) {
                    parseData.setTextContent(new String(page.getContentData()));
                } else {
                    parseData.setTextContent(
                            new String(page.getContentData(), page.getContentCharset()));
                }
                parseData.setOutgoingUrls(extractUrls(url, parseData.getTextContent()));
                page.setParseData(parseData);
            } catch (Exception e) {
                logger.error("{}, while parsing: {}", e.getMessage(), page.getWebURL().getURL());
                throw new ParseException();
            }
        } else { // isHTML

            HtmlParseData parsedData = this.htmlContentParser.parse(page, contextURL);

            if (page.getContentCharset() == null) {
                page.setContentCharset(parsedData.getContentCharset());
            }

            // Please note that identifying language takes less than 10 milliseconds
            LanguageIdentifier languageIdentifier = new LanguageIdentifier(parsedData.getText());
            page.setLanguage(languageIdentifier.getLanguage());

            page.setParseData(parsedData);

        }
    }

    private final Predicate<String> downloadUrlTest = new DownloadLinkTest();
    private Set<WebURL> extractUrls(String url, String input) {
        logger.info("==========================================\nExtracting URLs from: {}", url);
        Set<WebURL> urls = Net.extractUrls(input);
        if (Urls.UrlType.PollingUnit.equals(Urls.getType(url))) {
            logger.info("Is polling unit: true");
            if (urls.stream().map(WebURL::getURL).noneMatch(downloadUrlTest)) {
                logger.warn("Could not find download url in: {}\nFound: {}", url, urls);
            }
        }
        return urls;
//        Set<WebURL> extractedUrls = new HashSet<>();
//        if (input == null) {
//            return extractedUrls;
//        }
//        Document doc = Jsoup.parse(input);
//        final Elements elements = doc.select("a[href]|, iframe[src]");
//        for(Element element : elements) {
//            String link = element.attr("abs:href");
//            if(link == null || link.isEmpty()) {
//                link = element.attr("abs:src");
//            }
//            if(link == null || link.isEmpty()) {
//                continue;
//            }
//            WebURL webURL = new WebURL();
//            if (!link.startsWith("http")) {
//                link = "http://" + link;
//            }
//
//            webURL.setURL(link);
//            extractedUrls.add(webURL);
//        }
//
//        return extractedUrls;
    }
}
