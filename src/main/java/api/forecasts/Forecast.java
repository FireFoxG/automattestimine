package api;

import org.json.JSONObject;

public abstract class Forecast {
    private static String LONGITUDE_TYPE = "lon";
    private static String LATITUDE_TYPE = "lat";
    private static String TEMPERATURE_TYPE = "temp";
    private static String MAX_TEMPERATURE_TYPE = "temp_max";
    private static String MIN_TEMPERATURE_TYPE = "temp_min";
    private String cityName;
    private String country;
    private double longitude;
    private double latitude;
    private String description;
    private double minTemperature;
    private double maxTemperature;
    private double temperature;

    public Forecast(JSONObject json) {
        setPropertiesFromJson(json);
    }

    private void setPropertiesFromJson(JSONObject json) {
        cityName = getCityNameFromJson(json);
        longitude = getCoordinateFromJson(json, LATITUDE_TYPE);
        latitude = getCoordinateFromJson(json, LONGITUDE_TYPE);
        country = getCountryCodeFromJson(json);
        description = getDescriptionFromJson(json);
        setTemperatures(json);
    }

    private String getCityNameFromJson(JSONObject json) {
        return json.has("name") ? json.getString("name") : "NaN";
    }
    private double getCoordinateFromJson(JSONObject json, String type) { // can use Enum for "type" here, but too lazy
        if(json.has("coord")) {
            if(type.equals(LATITUDE_TYPE)) {
                return json.getJSONObject("coord").getDouble("lat");
            } else if(type.equals(LONGITUDE_TYPE)) {
                return json.getJSONObject("coord").getDouble("lon");
            }
        }
        return 0;
    }

    private double getTemperatureFromJson(JSONObject mainJson, String tempType) {
        return mainJson.has(tempType) ? mainJson.getDouble(tempType) : 0;
    }

    private String getCountryCodeFromJson(JSONObject json) {
        JSONObject sys = json.has("sys") ? json.getJSONObject("sys") : new JSONObject();
        return sys.has("country") ? sys.getString("country") : "NaN";
    }

    private String getDescriptionFromJson(JSONObject json) {
        JSONObject weatherInfo = json.has("weather") ? json.getJSONObject("weather") : new JSONObject();
        return weatherInfo.has("description") ? weatherInfo.getString("description") : "NaN";
    }

    private JSONObject getMainWeatherDataJson(JSONObject json) {
        return json.has("main") ? json.getJSONObject("main") : new JSONObject();
    }

    private void setTemperatures(JSONObject json) {
        JSONObject mainData = getMainWeatherDataJson(json);
        minTemperature = getTemperatureFromJson(mainData, MIN_TEMPERATURE_TYPE);
        maxTemperature = getTemperatureFromJson(mainData, MAX_TEMPERATURE_TYPE);
        temperature = getTemperatureFromJson(mainData, TEMPERATURE_TYPE);
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
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
