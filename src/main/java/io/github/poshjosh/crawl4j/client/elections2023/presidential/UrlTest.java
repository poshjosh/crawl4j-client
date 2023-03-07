package io.github.poshjosh.crawl4j.client.elections2023.presidential;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class UrlTest implements Predicate<String> {
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz|ico))$");
    public UrlTest() { }

    @Override
    public boolean test(String s) {
        return s.startsWith(Urls.BASE) && s.contains(Urls.PRESIDENTIAL_ID) &&
                !s.equals(Urls.TYPES) && !FILTERS.matcher(s).matches();
    }
}
