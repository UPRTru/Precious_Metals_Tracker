package prices.agent;

import net.minidev.json.JSONObject;
import prices.service.TypePrice;

import java.util.HashMap;

public interface Agent {

    HashMap<String, JSONObject> getPrices(TypePrice typePrice);
}
