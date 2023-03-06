package io.github.poshjosh.crawl4j.client;

import java.util.Collection;
import java.util.Optional;

public class StringUtil {

    private StringUtil() {}

    public static Optional<String> get(String s, String startTag, String endTag) {
        int start = s.indexOf(startTag);
        if (start == -1) {
            return Optional.empty();
        }
        int end = s.indexOf(endTag, start);
        if (end == -1) {
            return Optional.empty();
        }
        return Optional.of(s.substring(start, end + endTag.length()));
    }


    public static boolean startsWithAny(String s, Collection<String> collection) {
        if (collection.isEmpty()) {
            return true;
        }
        for(String prefix : collection) {
            if (s.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAny(String s, Collection<String> collection) {
        if (collection.isEmpty()) {
            return true;
        }
        for(String part : collection) {
            if (s.contains(part)) {
                return true;
            }
        }
        return false;
    }

    public static boolean endsWithAny(String s, Collection<String> collection) {
        if (collection.isEmpty()) {
            return true;
        }
        for(String suffix : collection) {
            if (s.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
