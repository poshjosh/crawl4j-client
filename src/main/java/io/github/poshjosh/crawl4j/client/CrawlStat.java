package io.github.poshjosh.crawl4j.client;

public class CrawlStat {
    private int totalProcessedPages;
    private long totalLinks;
    private int totalFilesDownloaded;
    private long totalBytesDownloaded;
    private String lastVisitUrl;

    public void recordSavedBytes(byte [] bytes) {
        this.incTotalFilesDownloaded(1);
        this.incTotalBytesDownloaded(bytes.length);
    }

    public int getTotalProcessedPages() {
        return totalProcessedPages;
    }

    public long getTotalLinks() {
        return totalLinks;
    }

    public int getTotalFilesDownloaded() {
        return totalFilesDownloaded;
    }

    public void incTotalProcessedPages(int count) {
        this.totalProcessedPages += count;
    }

    public void incTotalLinks(int count) {
        this.totalLinks += count;
    }

    public void incTotalFilesDownloaded(int count) {
        this.totalFilesDownloaded += count;
    }

    public long getTotalBytesDownloaded() {
        return totalBytesDownloaded;
    }

    public void incTotalBytesDownloaded(int count) {
        this.totalBytesDownloaded += count;
    }

    public String getLastVisitUrl() {
        return lastVisitUrl;
    }

    public void setLastVisitUrl(String lastVisitUrl) {
        this.lastVisitUrl = lastVisitUrl;
    }

    @Override
    public String toString() {
        return "CrawlStat{" + "pages=" + totalProcessedPages + ", links="
                + totalLinks + ", downloaded files=" + totalFilesDownloaded
                + ", bytes=" + totalBytesDownloaded + ", last Url='"
                + lastVisitUrl + '\'' + '}';
    }
}