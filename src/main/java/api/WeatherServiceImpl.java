package api;

import api.forecasts.CurrentForecast;
import api.forecasts.FiveDayForecast;
import api.helpers.FileFormatHelper;
import api.helpers.JsonReceiver;
import api.helpers.TextFileReader;
import api.helpers.TextFileWriter;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import static api.Config.CURRENT_FORECAST_TAG;
import static api.Config.FIVE_DAYS_FORECAST_TAG;

public class WeatherServiceImpl implements WeatherService {

    private static final String ONE_DAY_FORECAST_SERVICE_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";
    private static final String FIVE_DAYS_FORECAST_SERVICE_URL = "http://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s&units=metric";
    private static final String TEST_SERVICE_AVAILABILITY_URL = "http://api.openweathermap.org/data/2.5/forecast?appid=%s";

    private static final String API_KEY = "d431a365f6c0ac4c87b2fd03189dad41";
    private ForecastReader forecastReader = new ForecastReader();

    private enum forecastType {
        CURRENT,
        FIVEDAYS;
    }

    @Override
    public CurrentForecast getForecast(String cityName) throws NoDataFoundException {
        return new CurrentForecast(getCurrentForecastData(cityName));
    }

    @Override
    public FiveDayForecast getFiveDayForecast(String cityName) throws NoDataFoundException{
        return new FiveDayForecast(getFiveDaysForecastData(cityName));
    }

    private JSONObject getCurrentForecastJsonFromUrl(String cityName) {
        return getForecastFromUrl(ONE_DAY_FORECAST_SERVICE_URL, cityName, API_KEY);
    }

    private JSONObject getFiveDayForecastJsonFromUrl(String cityName) {
        return getForecastFromUrl(FIVE_DAYS_FORECAST_SERVICE_URL, cityName, API_KEY);
    }

    private JSONObject getForecastData(String cityName, forecastType type) {
        if(serviceIsAvailable()) {
            if(type.equals(forecastType.CURRENT)) {
                System.out.println("Fetching external current forecast data");
                return getCurrentForecastJsonFromUrl(cityName);
            } else {
                return getFiveDayForecastJsonFromUrl(cityName);
            }
        } else {
            JSONObject localData = type.equals(forecastType.CURRENT) ? forecastReader.readCurrentForecastData(cityName) : forecastReader.readFiveDayForecastData(cityName);
            if(localData.keySet().isEmpty()) {
                System.out.println("No data found locally and no connection available");
                return new JSONObject();
            } else {
                System.out.println("Local data found");
                return localData;
            }
        }
    }

    private JSONObject getCurrentForecastData(String cityName) {
        return getForecastData(cityName, forecastType.CURRENT);
    }

    private JSONObject getFiveDaysForecastData(String cityName) {
        return getForecastData(cityName, forecastType.FIVEDAYS);
    }

    private JSONObject getForecastFromUrl(String serviceUrl, String cityName, String key) {
        String url = String.format(serviceUrl, cityName, key);
        JSONObject jsonFromUrl = getResponseFromService(url);
        if(!jsonFromUrl.keySet().isEmpty()) {
            if(validResponse(jsonFromUrl)) {
                CitynamesTracker tracker = new CitynamesTracker();
                tracker.writeCityname(cityName);
            } else {
                System.out.println("City you requested was not found");
            }
        } else {
            System.out.println("Couldn't connect to external service. Check Internet connection?");
        }
        return jsonFromUrl;
    }

    private JSONObject getResponseFromService(String url) {
        try {
            return JsonReceiver.readJsonFromUrl(url);
        } catch (IOException e) {
            System.out.println("JsonReceiver couldn't get response from API");
            return new JSONObject();
        }
    }

    public boolean serviceIsAvailable() {
        try {
            JSONObject response = JsonReceiver.readJsonFromUrl(String.format(TEST_SERVICE_AVAILABILITY_URL, API_KEY));
            if(response.has("cod") && response.has("message")) {
                if(response.get("cod").toString().equals("400")) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }

    public boolean validResponse(JSONObject jsonFromUrl) {
        return jsonFromUrl.has("cod") && jsonFromUrl.getInt("cod") != 404;
    }

    public void updateLocalData() {
        TextFileReader reader = new TextFileReader();
        List<String> inputData = reader.readDataFromFile(Config.INPUT_FILENAME);
        for(String line : inputData) {
            String cityName = line;
            String filename = FileFormatHelper.addTxtIfNoFoundInFilename(cityName);
            JSONObject currentForecastJson = getForecastFromUrl(ONE_DAY_FORECAST_SERVICE_URL, cityName, API_KEY);
            JSONObject fiveDaysForecastJson = getForecastFromUrl(FIVE_DAYS_FORECAST_SERVICE_URL, cityName, API_KEY);
            if(currentForecastJson.keySet().isEmpty() || fiveDaysForecastJson.keySet().isEmpty()) {
                System.out.println("Data was not received for city " + cityName);
            } else {
                TextFileWriter writer = new TextFileWriter();
                writer.writeDataToFile(new FiveDayForecast(fiveDaysForecastJson)+"\n", filename);
                writer.appendDataToFile("current temp: " + new CurrentForecast(currentForecastJson).getCurrentTemperature() + "\n", filename);
                writer.appendDataToFile(CURRENT_FORECAST_TAG + currentForecastJson.toString()+"\n", filename);
                writer.appendDataToFile(FIVE_DAYS_FORECAST_TAG + fiveDaysForecastJson.toString()+"\n", filename);
            }
        }
    }
}
