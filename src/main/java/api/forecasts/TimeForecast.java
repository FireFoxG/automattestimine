package api.forecasts;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeForecast  {
    LocalDateTime time;
    private double minTemperature;
    private double maxTemperature;
    private double temperature;
    private String description;
    private static String TEMPERATURE_TYPE = "temp";
    private static String MAX_TEMPERATURE_TYPE = "temp_max";
    private static String MIN_TEMPERATURE_TYPE = "temp_min";
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TimeForecast(JSONObject json) {
        description = getDescriptionFromJson(json);
        setTemperatures(json);
        String dateTimeString = getDateTimeStringFromJson(json);
        this.time = LocalDateTime.parse(dateTimeString, formatter);
    }


    private void setTemperatures(JSONObject json) {
        JSONObject mainData = getMainWeatherDataJson(json);
        minTemperature = getTemperatureFromJson(mainData, MIN_TEMPERATURE_TYPE);
        maxTemperature = getTemperatureFromJson(mainData, MAX_TEMPERATURE_TYPE);
        temperature = getTemperatureFromJson(mainData, TEMPERATURE_TYPE);
    }

    private double getTemperatureFromJson(JSONObject mainJson, String tempType) {
        return mainJson.has(tempType) ? mainJson.getDouble(tempType) : 0;
    }

    private String getDescriptionFromJson(JSONObject json) {
        JSONObject weatherInfo = json.has("weather") ? (JSONObject) json.getJSONArray("weather").get(0) : new JSONObject();
        return weatherInfo.has("description") ? weatherInfo.getString("description") : "NaN";
    }

    private JSONObject getMainWeatherDataJson(JSONObject json) {
        return json.has("main") ? json.getJSONObject("main") : new JSONObject();
    }

    private String getDateTimeStringFromJson(JSONObject json) {
        return json.has("dt_txt") ? json.getString("dt_txt") : "";
    }

    public String getDescription() {
        return description;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getTemperature() {
        return temperature;
    }
}
