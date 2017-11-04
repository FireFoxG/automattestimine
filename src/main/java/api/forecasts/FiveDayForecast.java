package api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FiveDayForecast extends Forecast {
    List<TimeForecast> everyThreeHourForecasts;

    public FiveDayForecast(JSONObject json) {
        super(json);
        this.everyThreeHourForecasts = getDaysFromJson(json);
    }


    private List<TimeForecast> getDaysFromJson(JSONObject json) {
        List<TimeForecast> hourForecasts = new ArrayList<TimeForecast>();
        JSONArray listOfTimeForecasts = json.has("list") ? json.getJSONArray("list") : new JSONArray();
        if(listOfTimeForecasts.length() > 0) {
            for(Object timeForecastObj: listOfTimeForecasts) {
                JSONObject timeForecastJson = (JSONObject) timeForecastObj;
                TimeForecast timeForecast = new TimeForecast(timeForecastJson);
                hourForecasts.add(timeForecast);
            }
        }
        return hourForecasts;
    }
}
