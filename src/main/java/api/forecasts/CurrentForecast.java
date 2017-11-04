package api;

import org.json.JSONObject;

public class CurrentForecast extends Forecast {
    public CurrentForecast(JSONObject json) {
        super(json);
    }
}
