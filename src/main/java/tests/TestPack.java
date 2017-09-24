package tests;

import api.API;
import org.json.*;
import org.junit.jupiter.api.Test;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
On väline API, kuskohast saab pärida linnade ilma infot:
Hetke ilm ja lisaks 3 päeva ilmaennustus (API väljundis antakse ennustus iga kolme tunni tagant).

Vaja on leida:
* hetke temperatuur
* 3päeva ilmaennustusest leida iga päeva kõrgeim ja madalaim temperatuur
* Koordinaadid (GEO) kujul xxx:yyy

API väljund on JSON formaadis

 */

public class TestPack {

    @Test
    void isResponseJson() {
        API api = new API();
        JSONObject response = api.getForecast("Tallinn");
        assertEquals(true, response.toString().startsWith("{") && response.toString().endsWith("}"));
    }

    @Test
    void areBothForecastTypesPresent() {
        API api = new API();
        JSONObject response = api.getForecast("Tallinn");
        assertEquals(true, response.has("current") && response.has("nextDays"));
    }

    @Test
    void isCurrentTemperatureReal() {
        API api = new API();
        JSONObject response = api.getForecast("Tallinn");
        Integer currentTemperature = (Integer) ((JSONObject) response.get("current")).get("temperature");
        assertEquals(true, currentTemperature > -50 && currentTemperature < 60 ? true : false);
    }

    @Test
    void isThreeDayForecastTemperatureReal() {
        API api = new API();
        boolean complexBoolean =  true;
        JSONObject response = api.getForecast("Tallinn");
        JSONObject nextThreeDays = (JSONObject) response.get("nextDays");
        for(String dayKey : nextThreeDays.keySet()) {
            JSONObject dayForecast = (JSONObject) nextThreeDays.get(dayKey);
            for(String hourKey : dayForecast.keySet()) {
                JSONObject hourInfo = (JSONObject) dayForecast.get(hourKey);
                Integer hourTemperature = (Integer) hourInfo.get("temperature");
                if(hourTemperature < -50 && hourTemperature > 60) {
                    complexBoolean = false;
                }
            }
        }
        Integer currentTemperature = (Integer) ((JSONObject) response.get("current")).get("temperature");
        assertEquals(true, currentTemperature > -50 && currentTemperature < 60 && complexBoolean);
    }

    @Test
    void isRightResponseCity() {
        API api = new API();
        String someCityName = "London";
        JSONObject response = api.getForecast(someCityName);
        assertEquals(someCityName, (String) response.get("city"));
    }

    @Test
    void isRightCoordinateFormat() {
        API api = new API();
        JSONObject response = api.getForecast("Tallinn");
        Pattern pattern = Pattern.compile("^\\d{3}[:]\\d{3}$");
        Matcher matcher = pattern.matcher((String) response.get("coordinates"));

        assertEquals(true, matcher.find());
    }

}
