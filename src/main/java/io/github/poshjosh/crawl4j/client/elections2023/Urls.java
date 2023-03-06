package io.github.poshjosh.crawl4j.client.elections2023;

public final class Urls {
    private Urls() { }

    public enum UrlType {PollingUnit, Ward, LocalGovt, State, None}

    public static UrlType getType(String url) {
        if (url.startsWith("https://inecelectionresults.ng/elections/63f8f25b594e164f8146a213/context/pus/lga/")) {
            return UrlType.PollingUnit;
        }
        if (url.startsWith("https://inecelectionresults.ng/elections/63f8f25b594e164f8146a213/context/ward/lga/")) {
            return UrlType.Ward;
        }
        if (url.startsWith("https://inecelectionresults.ng/elections/63f8f25b594e164f8146a213?state=")) {
            return UrlType.LocalGovt;
        }
        if (url.equals("https://inecelectionresults.ng/pres/elections/63f8f25b594e164f8146a213?type=pres")) {
            return UrlType.State;
        }
        return UrlType.None;
    }

    public static final String BASE = "https://inecelectionresults.ng";
    public static final String TYPES = "https://www.inecelectionresults.ng/elections/types";
    public static final String PRESIDENTIAL_ID = "/elections/63f8f25b594e164f8146a213";
    public static final String PRESIDENTIAL_SEED = BASE + "/pres" + PRESIDENTIAL_ID + "?type=pres";

    public static final String [] EXTENSIONS = {"pdf", "jpg"};
}
