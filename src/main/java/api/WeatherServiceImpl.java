package api;

import api.forecasts.CurrentForecast;
import api.forecasts.FiveDayForecast;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class WeatherServiceImpl implements WeatherService {

    private static final String ONE_DAY_SERVICE_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";
    private static final String FIVE_DAYS_SERVICE_URL = "http://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s&units=metric";

    private static final String API_KEY = "d431a365f6c0ac4c87b2fd03189dad41";
    private static final String INPUT_FILENAME = "input.txt";


    @Override
    public JSONObject getForecast(String cityName) {
        return getForecastFromUrl(ONE_DAY_SERVICE_URL, cityName, API_KEY);
    }

    @Override
    public CurrentForecast getForecast() throws NoDataFoundException {
        return new CurrentForecast(getForecastFromUrl(ONE_DAY_SERVICE_URL, API_KEY));
    }

    @Override
    public JSONObject getFiveDayForecast(String cityName) {
        return getForecastFromUrl(FIVE_DAYS_SERVICE_URL, cityName, API_KEY);
    }

    @Override
    public FiveDayForecast getFiveDayForecast() throws NoDataFoundException{
        return new FiveDayForecast(getForecastFromUrl(FIVE_DAYS_SERVICE_URL, API_KEY));
    }

    private JSONObject getForecastFromUrl(String serviceUrl, String cityName, String key) {
        String url = String.format(serviceUrl, cityName, key);
        return getResponseFromService(url);
    }

    private JSONObject getForecastFromUrl(String serviceUrl, String key) throws NoDataFoundException {
        TextFileReader reader = new TextFileReader();
        List<String> inputData = reader.readDataFromFile(INPUT_FILENAME);
        if(inputData.size() > 0) {
            String cityName = inputData.get(0);
            return getForecastFromUrl(serviceUrl, cityName, key);
        } else {
            throw new NoDataFoundException("Couldn't read any line from: " + INPUT_FILENAME);
        }
    }

    private JSONObject getResponseFromService(String url) {
        try {
            return JsonReceiver.readJsonFromUrl(url);
        } catch (IOException e) {
            System.out.println("JsonReceiver couldn't get response from API");
            return new JSONObject();
        }
    }


}
