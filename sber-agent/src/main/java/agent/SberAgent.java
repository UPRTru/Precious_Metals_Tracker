package agent;

import com.precious.shared.model.Metal;
import net.minidev.json.JSONArray;

import java.util.HashMap;

public interface SberAgent {

    public HashMap<Metal, JSONArray> getPrices();
}
