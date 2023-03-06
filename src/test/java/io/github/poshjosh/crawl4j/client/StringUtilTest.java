package io.github.poshjosh.crawl4j.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    @ParameterizedTest
    @CsvSource({
            "<div/><div></div><div/>,<div>,</div>,<div></div>",
            "<Error/><Error>Error</Error><Error/>,<Error>,</Error>,<Error>Error</Error>"
    })
    void get(String input, String startTag, String endTag, String expected) {
        String result = StringUtil.get(input, startTag, endTag).orElse(null);
        assertEquals(expected, result);
    }
}