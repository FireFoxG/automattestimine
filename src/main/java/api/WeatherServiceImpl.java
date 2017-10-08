package api;

import org.json.JSONObject;

import java.io.IOException;

public class WeatherServiceImpl implements WeatherService {

    private static final String ONE_DAY_SERVICE_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";
    private static final String FIVE_DAYS_SERVICE_URL = "http://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s&units=metric";
    private static final String API_KEY = "d431a365f6c0ac4c87b2fd03189dad41";


    @Override
    public JSONObject getForecast(String cityName) {
        String url = String.format(ONE_DAY_SERVICE_URL, cityName, API_KEY);
        System.out.println(url);
        return getResponseFromService(url);
    }

    @Override
    public JSONObject getThreeDayForecast(String cityName) {
        String url = String.format(FIVE_DAYS_SERVICE_URL, cityName, API_KEY);
        System.out.println(url);
        return getResponseFromService(url);
    }

    private JSONObject getResponseFromService(String url) {
        try {
            return JsonReceiver.readJsonFromUrl(url);
        } catch (Exception e) {
            return new JSONObject();
        }
    }


}
