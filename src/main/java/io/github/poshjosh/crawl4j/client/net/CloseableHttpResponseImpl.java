package io.github.poshjosh.crawl4j.client.net;

import org.apache.http.ProtocolVersion;
import org.apache.http.ReasonPhraseCatalog;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHttpResponse;

import java.util.Locale;

public class CloseableHttpResponseImpl extends BasicHttpResponse implements CloseableHttpResponse {

    public CloseableHttpResponseImpl(StatusLine statusline, ReasonPhraseCatalog catalog, Locale locale) {
        super(statusline, catalog, locale);
    }

    public CloseableHttpResponseImpl(StatusLine statusline) {
        super(statusline);
    }

    public CloseableHttpResponseImpl(ProtocolVersion ver, int code, String reason) {
        super(ver, code, reason);
    }

    @Override
    public void close() { }
}
