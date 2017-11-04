package api.forecasts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FiveDayForecast extends Forecast {
    List<TimeForecast> everyThreeHourForecasts;

    public FiveDayForecast(JSONObject json) {
        super(json.has("city") ? json.getJSONObject("city") : new JSONObject());
        this.everyThreeHourForecasts = getDaysFromJson(json);
    }

    @Override
    protected String getCountryCodeFromJson(JSONObject json) {
        return json.has("country") ? json.getString("country") : "NaN";
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

    private int getIndexOfNextDayTimeForecast() {
        int counter = 0;
        for(TimeForecast f : everyThreeHourForecasts) {
            if(f.time.getHour() == 0) {
                counter = everyThreeHourForecasts.indexOf(f);
                break;
            }
        }
        return counter;
    }

    public double getAverageTemperatureForNextThreeDays() {
        double sum = 0;
        int counter = getIndexOfNextDayTimeForecast();
        for(int i = counter; i<counter + 24; i++) { // 3h step -> 8 steps per day * 3 days
            TimeForecast forecast = everyThreeHourForecasts.get(i);
            sum+= forecast.getTemperature();
        }
        return sum/24;
    }

    public double getMaxTempForNextThreeDays() {
        double highest = -999;
        int counter = getIndexOfNextDayTimeForecast();
        for(int i = counter; i<counter + 24; i++) {
            TimeForecast forecast = everyThreeHourForecasts.get(i);
            double temp = forecast.getMaxTemperature();
            if(temp > highest) {
                highest = temp;
            }
        }
        return highest;
    }

    public double getMinTempForNextThreeDays() {
        double lowest = 999;
        int counter = getIndexOfNextDayTimeForecast();
        for(int i = counter; i<counter + 24; i++) {
            TimeForecast forecast = everyThreeHourForecasts.get(i);
            double temp = forecast.getMinTemperature();
            if(temp < lowest) {
                lowest = temp;
            }
        }
        return lowest;
    }
}
