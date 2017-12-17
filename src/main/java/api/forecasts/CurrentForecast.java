package api.forecasts;

import org.json.JSONObject;

import java.time.LocalDateTime;

public class CurrentForecast extends Forecast {

    private TimeForecast currentTimeForecast;

    public CurrentForecast(JSONObject json) {
        super(json);
        json.put("dt_txt", LocalDateTime.now().format(TimeForecast.formatter));
        currentTimeForecast = new TimeForecast(json);
    }

    public double getCurrentTemperature() {
        return currentTimeForecast.getTemperature();
    }


    public double getMaximumTemperature() {
        return currentTimeForecast.getTemperature();
    }


    public double getMinimumtTemperature() {
        return currentTimeForecast.getTemperature();
    }


    public String getWeatherDescription() {
        return currentTimeForecast.getDescription();
    }


}
