package io.github.poshjosh.crawl4j.client;

public class CrawlStat {

    private String lastVisitUrl;
    private int totalProcessedPages;
    private long totalLinks;
    private int totalFilesDownloaded;
    private long totalTextSize;

    public String getLastVisitUrl() {
        return lastVisitUrl;
    }

    public void setLastVisitUrl(String lastVisitUrl) {
        this.lastVisitUrl = lastVisitUrl;
    }

    public int getTotalProcessedPages() {
        return totalProcessedPages;
    }

    public void setTotalProcessedPages(int totalProcessedPages) {
        this.totalProcessedPages = totalProcessedPages;
    }

    public long getTotalLinks() {
        return totalLinks;
    }

    public void setTotalLinks(long totalLinks) {
        this.totalLinks = totalLinks;
    }

    public int getTotalFilesDownloaded() {
        return totalFilesDownloaded;
    }

    public void setTotalFilesDownloaded(int totalFilesDownloaded) {
        this.totalFilesDownloaded = totalFilesDownloaded;
    }

    public long getTotalTextSize() {
        return totalTextSize;
    }

    public void setTotalTextSize(long totalTextSize) {
        this.totalTextSize = totalTextSize;
    }

    public void incTotalProcessedPages() {
        this.totalProcessedPages++;
    }

    public void incTotalLinks(int count) {
        this.totalLinks += count;
    }

    public void incTotalFilesDownloaded() {
        this.totalFilesDownloaded++;
    }

    public void incTotalTextSize(int count) {
        this.totalTextSize += count;
    }
}