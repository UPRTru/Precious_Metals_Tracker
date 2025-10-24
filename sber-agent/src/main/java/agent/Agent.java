package agent;

import net.minidev.json.JSONObject;

import java.util.HashMap;

public interface Agent {

    public HashMap<String, JSONObject> getPrices();
}
