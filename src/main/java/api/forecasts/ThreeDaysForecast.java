package api.forecasts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ThreeDaysForecast extends Forecast {
    List<TimeForecast> everyThreeHourForecasts;
    List<DayForecast> nextThreeDaysForecast;

    public ThreeDaysForecast(JSONObject json) {
        super(json.has("city") ? json.getJSONObject("city") : new JSONObject());
        this.everyThreeHourForecasts = getTimeForecastsFromJson(json);
        List<DayForecast> dayForecasts = getDayForecasts(everyThreeHourForecasts);
        this.nextThreeDaysForecast = dayForecasts.size() >= 3 ? dayForecasts.subList(0,3) : dayForecasts;
    }

    @Override
    protected String getCountryCodeFromJson(JSONObject json) {
        return json.has("country") ? json.getString("country") : "NaN";
    }

    @Override
    public String getCityNameFromJson(JSONObject json) {
        return json.has("name") ? json.getString("name") : "NaN";
    }

    private List<DayForecast> getDayForecasts(List<TimeForecast> timeForecasts) {
        List<DayForecast> dayForecasts = new ArrayList<>();
        int counter = getIndexOfNextDayTimeForecast(timeForecasts);
        if(timeForecasts.size() > 0) {
            List<TimeForecast> actualForecomingTimeForecasts = timeForecasts.subList(counter, timeForecasts.size());
            List<TimeForecast> timeForecastsForOneDay = new ArrayList<>();
            int dateToStart = actualForecomingTimeForecasts.get(0).time.getDayOfMonth();
            for(TimeForecast t : actualForecomingTimeForecasts) {
                int dayOfCurrentTimeForecast = t.time.getDayOfMonth();
                if(dayOfCurrentTimeForecast == dateToStart) {
                    timeForecastsForOneDay.add(t);
                } else {
                    dayForecasts.add(new DayForecast(timeForecastsForOneDay));
                    dateToStart = dayOfCurrentTimeForecast;
                    timeForecastsForOneDay = new ArrayList<>();
                }
            }
        }
        return dayForecasts;
    }

    private List<TimeForecast> getTimeForecastsFromJson(JSONObject json) {
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

    private int getIndexOfNextDayTimeForecast(List<TimeForecast> timeForecasts) {
        int counter = 0;
        for(TimeForecast f : timeForecasts) {
            if(f.time.getHour() == 0) {
                counter = timeForecasts.indexOf(f);
                break;
            }
        }
        return counter;
    }

    public double getAverageTemperatureForNextThreeDays() {
        double sum = 0;
        int NUMBER_OF_3_HOUR_STEPS = 24;
        int counter = getIndexOfNextDayTimeForecast(everyThreeHourForecasts);
        for(int i = counter; i<counter + NUMBER_OF_3_HOUR_STEPS; i++) { // 3h step -> 8 steps per day * 3 days
            TimeForecast forecast = everyThreeHourForecasts.get(i);
            sum+= forecast.getTemperature();
        }
        return sum/NUMBER_OF_3_HOUR_STEPS;
    }

    public double getMaxTempForNextThreeDays() {
        double highest = -999;
        int counter = getIndexOfNextDayTimeForecast(everyThreeHourForecasts);
        if(!everyThreeHourForecasts.isEmpty()) {
            for(int i = counter; i<counter + 24; i++) {
                TimeForecast forecast = everyThreeHourForecasts.get(i);
                double temp = forecast.getMaxTemperature();
                if(temp > highest) {
                    highest = temp;
                }
            }
        }
        return highest;
    }

    public double getMinTempForNextThreeDays() {
        double lowest = 999;
        int counter = getIndexOfNextDayTimeForecast(everyThreeHourForecasts);
        if(!everyThreeHourForecasts.isEmpty()) {
            for (int i = counter; i < counter + 24; i++) {
                TimeForecast forecast = everyThreeHourForecasts.get(i);
                double temp = forecast.getMinTemperature();
                if (temp < lowest) {
                    lowest = temp;
                }
            }
        }
        return lowest;
    }

    public List<DayForecast> getNextThreeDaysForecast() {
        return nextThreeDaysForecast;
    }

    @Override
    public String toString() {
        String additionalTimeForecasts = "";
        for(TimeForecast tf : everyThreeHourForecasts) {
            additionalTimeForecasts+= tf.time + "\n" +
                    " max temp: " + tf.getMaxTemperature() + "\n" +
                    " min temp: " + tf.getMinTemperature() +"\n";
            if(everyThreeHourForecasts.indexOf(tf) == 2) {
                break;
            }
        }

        return "Linna nimi: " + getCityName() + "\n" +
                "koordinatid: " + getLongitude() + " " + getLatitude() + "\n" +
                additionalTimeForecasts;
    }
}
