package api;

import api.forecasts.CurrentForecast;
import api.forecasts.FiveDayForecast;
import org.json.JSONObject;

public interface WeatherService {
    public JSONObject getForecast(String cityName);

    public JSONObject getFiveDayForecast(String cityName);

    public CurrentForecast getForecast() throws NoDataFoundException;

    public FiveDayForecast getFiveDayForecast() throws NoDataFoundException;
}
