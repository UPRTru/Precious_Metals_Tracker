package prices.agent;

import net.minidev.json.JSONObject;
import prices.agent.sber.currency.SberAgentCurrencyConfig;
import prices.agent.sber.metal.SberAgentMetalConfig;

import java.util.HashMap;

public interface Agent {

    HashMap<String, JSONObject> getPrices(SberAgentCurrencyConfig url);
    HashMap<String, JSONObject> getPrices(SberAgentMetalConfig url);
}
