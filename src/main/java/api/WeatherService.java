package api;

import org.json.JSONObject;

public interface WeatherService {
    public JSONObject getForecast(String cityName);

    public JSONObject getThreeDayForecast(String cityName);
}
