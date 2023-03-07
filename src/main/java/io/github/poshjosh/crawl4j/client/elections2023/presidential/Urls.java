package io.github.poshjosh.crawl4j.client.elections2023.presidential;

public final class Urls {
    private Urls() { }

    public static final String BASE = "https://inecelectionresults.ng";
    public static final String TYPES = "https://www.inecelectionresults.ng/elections/types";
    public static final String PRESIDENTIAL_ID = "/elections/63f8f25b594e164f8146a213";
    public static final String PRESIDENTIAL = BASE + PRESIDENTIAL_ID;
    public static final String [] EXTENSIONS = {"pdf", "jpg"};

    public enum UrlType {PollingUnit, Ward, LocalGovt, State, None}
    private static final String POLLING_UNIT_PREFIX = PRESIDENTIAL + "/context/pus/lga/";
    private static final String WARD_PREFIX = PRESIDENTIAL + "/context/ward/lga/";
    private static final String LOCAL_GOVT_PREFIX = PRESIDENTIAL + "?state=";
    private static final String STATE = PRESIDENTIAL + "?type=pres";

    public static UrlType getType(String url) {
        if (url.startsWith(POLLING_UNIT_PREFIX)) {
            return UrlType.PollingUnit;
        }
        if (url.startsWith(WARD_PREFIX)) {
            return UrlType.Ward;
        }
        if (url.startsWith(LOCAL_GOVT_PREFIX)) {
            return UrlType.LocalGovt;
        }
        if (url.equals(STATE)) {
            return UrlType.State;
        }
        return UrlType.None;
    }
}
