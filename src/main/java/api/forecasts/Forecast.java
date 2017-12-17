package api.forecasts;

import org.json.JSONObject;

public abstract class Forecast {
    private static String LONGITUDE_TYPE = "lon";
    private static String LATITUDE_TYPE = "lat";

    private String cityName;
    private String country;
    private double longitude;
    private double latitude;


    public Forecast(JSONObject json) {
        setPropertiesFromJson(json);
    }

    protected void setPropertiesFromJson(JSONObject json) {
        cityName = getCityNameFromJson(json);
        longitude = getCoordinateFromJson(json, LATITUDE_TYPE);
        latitude = getCoordinateFromJson(json, LONGITUDE_TYPE);
        country = getCountryCodeFromJson(json);
    }

    protected String getCityNameFromJson(JSONObject json) {
        return json.has("name") ? json.getString("name") : "NaN";
    }

    protected double getCoordinateFromJson(JSONObject json, String type) { // can use Enum for "type" here, but too lazy
        if(json.has("coord")) {
            if(type.equals(LATITUDE_TYPE)) {
                return json.getJSONObject("coord").getDouble("lat");
            } else if(type.equals(LONGITUDE_TYPE)) {
                return json.getJSONObject("coord").getDouble("lon");
            }
        }
        return 0;
    }
    protected String getCountryCodeFromJson(JSONObject json) {
        JSONObject sys = json.has("sys") ? json.getJSONObject("sys") : new JSONObject();
        return sys.has("country") ? sys.getString("country") : "NaN";
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

    @Override
    public String toString() {
        return "Forecast{" +
                "cityName='" + cityName + '\'' +
                ", country='" + country + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
