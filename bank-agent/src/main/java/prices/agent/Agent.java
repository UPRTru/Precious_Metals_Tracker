package prices.agent;

import net.minidev.json.JSONObject;

import java.util.HashMap;

public interface Agent {

    HashMap<String, JSONObject> getPrices();
}
