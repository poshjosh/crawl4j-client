package io.github.poshjosh.crawl4j.client.elections2023;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PathUtilTest {

    @ParameterizedTest
    @CsvSource({
            "http://abc/def$/ghi%/jkl?key=val&accept=false,/abc/def/ghi/jklkeyvalacceptfalse",
            "https://docs.inecelectionresults.net/elections_prod/1292/state/1/lga/3103/ward/17526/pu/126567/126567-1677666960.pdf,/docs.inecelectionresults.net/elections_prod/1292/state/1/lga/3103/ward/17526/pu/126567/126567-1677666960.pdf"
    })
    void resolvePathForUrl(String input, String expected) {
        assertEquals(expected, PathUtil.resolvePathForUrl(input));
    }
}