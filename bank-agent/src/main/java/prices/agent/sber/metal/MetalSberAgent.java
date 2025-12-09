package prices.agent.sber.metal;

import com.precious.shared.enums.CurrentPrice;
import com.precious.shared.enums.Metal;
import net.minidev.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;
import prices.agent.Agent;
import prices.agent.AgentConfig;
import prices.agent.EnumAgentsConfig;
import prices.agent.WebDriverSupport;
import prices.utils.JsonUtils;

import java.util.HashMap;

@Component(MetalSberAgent.AGENT_NAME)
public class MetalSberAgent implements Agent {

    public static final String AGENT_NAME = "sber agent metal";

    private final AgentConfig agentConfig;
    private final WebDriverSupport webDriverSupport;

    public MetalSberAgent() {
        agentConfig = EnumAgentsConfig.SBER_METAL.getAgentConfig();
        webDriverSupport = new WebDriverSupport();
    }

    @Override
    public HashMap<String, JSONObject> getPrices() {
        webDriverSupport.createDriver();
        webDriverSupport.goToPage(agentConfig.getUrl());
        HashMap<String, JSONObject> result = getMetalsPrices();
        webDriverSupport.closeDriver();
        return result;
    }

    private HashMap<String, JSONObject> getMetalsPrices() {
        HashMap<String, JSONObject> result = new HashMap<>();
        for (Metal metal : Metal.values()) {
            result.putAll(getMetalPrices(metal));
        }
        return result;
    }

    private HashMap<String, JSONObject> getMetalPrices(Metal metal) {
        JSONObject jsonObject = JsonUtils.createJsonObject(metal.name());
        getJsonMetalPrice(jsonObject, metal, CurrentPrice.BUY);
        getJsonMetalPrice(jsonObject, metal, CurrentPrice.SELL);
        HashMap<String, JSONObject> result = new HashMap<>();
        result.put(metal.name(), jsonObject);
        return result;
    }

    private void getJsonMetalPrice(JSONObject jsonObject, Metal metal, CurrentPrice currentPrice) {
        WebElement webElement = getWebElement(metal, currentPrice);
        JsonUtils.appendPrice(jsonObject, currentPrice, webElement.getText());
    }

    private WebElement getWebElement(Metal metal, CurrentPrice currentPrice) {
        String index;
        switch (currentPrice) {
            case BUY:
                index = agentConfig.getIndexBuy();
                break;
            case SELL:
                index = agentConfig.getIndexSell();
                break;
            default:
                index = "";
        }

        return webDriverSupport.getWebDriver().until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath(String.format(agentConfig.getWebElement(), metal.getDisplayName(), index))
                )
        );
    }
}