package api;

import api.forecasts.CurrentForecast;
import api.forecasts.FiveDayForecast;
import org.json.JSONObject;

public interface WeatherService {

    public CurrentForecast getForecast(String cityName) throws NoDataFoundException;

    public FiveDayForecast getFiveDayForecast(String cityName) throws NoDataFoundException;

    public boolean serviceIsAvailable();

    public void updateLocalData();
}
