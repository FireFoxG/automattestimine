package tests;

import api.NoDataFoundException;
import api.WeatherService;
import api.WeatherServiceImpl;
import api.forecasts.CurrentForecast;
import api.forecasts.FiveDayForecast;
import api.forecasts.Forecast;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NewTestPack {
    private static final String DEFAULT_CITY = "Tallinn";

    @Test
    void isCurrentTemperatureReal() throws NoDataFoundException{
        WeatherService api = new WeatherServiceImpl();
        CurrentForecast response = api.getForecast();
        Double currentTemperature = response.getCurrentTemperature();
        assertEquals(true, currentTemperature > -90 && currentTemperature < 60 ? true : false); // minimum temperature recorded on Earth -89.2, maximum 56.7
    }

    @Test
    void isRightResponseCity() throws NoDataFoundException{
        WeatherService api = new WeatherServiceImpl();
        CurrentForecast response = api.getForecast();
        assertEquals(DEFAULT_CITY, response.getCityName());
    }
}
