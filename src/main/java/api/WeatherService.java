package api;

import api.forecasts.CurrentForecast;
import api.forecasts.ThreeDaysForecast;

public interface WeatherService {

    public CurrentForecast getForecast(String cityName) throws NoDataFoundException;

    public ThreeDaysForecast getFiveDayForecast(String cityName) throws NoDataFoundException;

    public boolean serviceIsAvailable();

    public void updateLocalData();
}
