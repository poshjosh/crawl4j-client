package io.github.poshjosh.crawl4j.client.elections2023.presidential;

import io.github.poshjosh.crawl4j.client.StringUtil;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DownloadLinkTest implements Predicate<String> {

    // https://inec-cvr-cache.s3.eu-west-1.amazonaws.com/cached/results/618239/result_160723_1677495620_thumb.jpg
    private static final String DOC_STORAGE = "https://docs.inecelectionresults.";

    // https://docs.inecelectionresults.net/elections_prod/1292/state/1/lga/3103/ward/17526/pu/126567/126567-1677666960.pdf
    private static final String AWS_S3_STORAGE = ".amazonaws.com/cached/results";

    private static final Set<String> EXTENSIONS = Arrays.stream(Urls.EXTENSIONS).map(ext -> "." + ext).collect(Collectors.toSet());

    public DownloadLinkTest() { }

    @Override
    public boolean test(String s) {
        return (s.startsWith(DOC_STORAGE) || s.contains(AWS_S3_STORAGE)) && StringUtil.endsWithAny(s, EXTENSIONS);
    }
}
