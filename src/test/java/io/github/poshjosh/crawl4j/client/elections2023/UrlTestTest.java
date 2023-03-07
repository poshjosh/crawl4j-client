package io.github.poshjosh.crawl4j.client.elections2023;

import io.github.poshjosh.crawl4j.client.elections2023.presidential.UrlTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UrlTestTest {

    private final UrlTest urlTest = new UrlTest();

    @ParameterizedTest
    @ValueSource(strings = {
            "https://inecelectionresults.ng/pres/elections/63f8f25b594e164f8146a213?type=pres",
            "https://inecelectionresults.ng/elections/63f8f25b594e164f8146a213?state=1",
            "https://inecelectionresults.ng/elections/63f8f25b594e164f8146a213?state=",
            "https://inecelectionresults.ng/elections/63f8f25b594e164f8146a213/context/ward/lga/5f0f397a4d89fc3a883de0c0",
            "https://inecelectionresults.ng/elections/63f8f25b594e164f8146a213/context999/ward/lga/5f0f397a4d89fc3a883de0c0",
            "https://inecelectionresults.ng/elections/63f8f25b594e164f8146a213/context/pus/lga/5f0f397a4d89fc3a883de0c0/ward/5f0f3a368f77bb3acad08d99",
            "https://inecelectionresults.ng/elections/63f8f25b594e164f8146a213/pu/63f8f28b594e164f8146a3b5/document",
    })
    void shouldAcceptValidUrl(String url) {
        assertTrue(urlTest.test(url), "URL: " + url);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "http://inecelectionresults.ng/pres/elections/63f8f25b594e164f8146a213?type=pres",
            "https://abc/pres/elections/63f8f25b594e164f8146a213?type=pres",
    })
    void shouldRejectInvalidUrl(String url) {
        assertFalse(urlTest.test(url), "URL: " + url);
    }
}