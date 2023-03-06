package io.github.poshjosh.crawl4j.client.net;

import io.github.poshjosh.crawl4j.client.elections2023.Urls;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class ElementClickableConditions implements ExpectedCondition<WebElement>{

    private static final Logger logger = LoggerFactory.getLogger(ElementClickableConditions.class);

    // https://inecelectionresults.ng/elections/63f8f25b594e164f8146a213/context/pus/lga/5f0f397a4d89fc3a883de0c0/ward/5f0f3a368f77bb3acad08d99
    // <button _ngcontent-bhj-c48="" class="btn btn-success" tabindex="0">View result</button>
    private static ExpectedCondition<WebElement> pollingUnit =
            elementToBeClickable("button", "btn btn-success", 0, "View result");

    // https://inecelectionresults.ng/elections/63f8f25b594e164f8146a213/context/ward/lga/5f0f397a4d89fc3a883de0c0
    // <button _ngcontent-bmv-c48="" class="btn btn-sm btn-outline-primary" tabindex="0"><i _ngcontent-bmv-c48="" class="fa-solid fa-location-dot me-2"></i> Polling units</button>
    private static ExpectedCondition<WebElement> ward =
            elementToBeClickable("button", "btn btn-sm btn-outline-primary", 0,"Polling units");

    // https://inecelectionresults.ng/elections/63f8f25b594e164f8146a213?state=1
    // <button _ngcontent-xpe-c48="" class="btn btn-sm btn-outline-primary" tabindex="0"><i _ngcontent-xpe-c48="" class="fa-solid fa-location-dot me-2"></i> View Wards</button>
    private static ExpectedCondition<WebElement> localGovt =
            elementToBeClickable("button", "btn btn-sm btn-outline-primary", 0, "View Wards");

    // https://inecelectionresults.ng/pres/elections/63f8f25b594e164f8146a213?type=pres
    // <a _ngcontent-vdj-c55="" class="btn btn-sm btn-outline-success">View results</a>
    private static ExpectedCondition<WebElement> state =
            elementToBeClickable("a", "btn btn-sm btn-outline-success", -1,"View results");

    @Override
    public WebElement apply(WebDriver webDriver) {
        final String url = webDriver.getCurrentUrl();
        final Urls.UrlType urlType = Urls.getType(url);
        logger.debug("Checking condition for: {} {}", urlType, url);
        switch(urlType) {
            case PollingUnit: return pollingUnit.apply(webDriver);
            case Ward: return ward.apply(webDriver);
            case LocalGovt: return localGovt.apply(webDriver);
            case State: return state.apply(webDriver);
            default: return null;
        }
    }

    private static ExpectedCondition<WebElement> elementToBeClickable(String tag, String className, int tabIndex, String textToContain) {
        final String cssSelector = tag + "[class='" + className + "']:not([routerlink])"  + (tabIndex < 0 ? "" : "[tabindex='" + tabIndex+"']");
        final By locator = By.cssSelector(cssSelector);
        return new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver driver) {
                WebElement result = null;
                List<WebElement> elements = Collections.emptyList();
                try {
                    elements = driver.findElements(locator);
                    for (WebElement element : elements) {
                        if (isClickable(element, textToContain)) {
                            result = element;
                            break;
                        }
                    }
                } catch (StaleElementReferenceException | NoSuchElementException e) {
                    logger.warn("{}", e.toString());
                }
                if (result == null) {
                    logger.warn("Clickable: false, for text: '{}' in {} elements: {}", textToContain, elements.size(), cssSelector);
                }
                return result;
            }

            @Override
            public String toString() {
                return "element to be clickable: " + locator;
            }
        };
    }

    private static boolean isClickable(WebElement element, String textToContain) {
        logger.info("Displayed: {}, enabled: {}, text: {}", element.isDisplayed(), element.isEnabled(), element.getText());
        if (!element.isDisplayed() || !element.isEnabled()) {
            return false;
        }
        String text = element.getText();
        if (text == null || text.isEmpty()) {
            return false;
        }
        return text.contains(textToContain);
    }
}
