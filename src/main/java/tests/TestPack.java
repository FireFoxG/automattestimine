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

/*
    I assume that JSON response is in form of :

    {"city":"Tallinn",
     "coordinates":"120:588",
     "current": {
        "temperature": "20",
        "clouds:"10%",
        etc..},
     "nextDays": {
        "day1": {
            "minTemp": "7"
            "maxTemp": "14"
            "0": {
                "temperature": "10",
                "clouds:"10%"},
            "3": {
                "temperature": "12",
                "clouds:"10%"},
            etc..
            },
            day2": {
            "minTemp": "5"
            "maxTemp": "23"
            "0": {
                "temperature": "5",
                "clouds:"10%"},
            "3": {
                "temperature": "10",
                "clouds:"10%"},
            etc..
            },
            etc..
        }
     }
  }

*/

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
        assertEquals(true, currentTemperature > -90 && currentTemperature < 60 ? true : false); // minimum temperature recorded on Earth -89.2, maximum 56.7
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
                if(hourTemperature < -90 && hourTemperature > 60) {
                    complexBoolean = false;
                    break;
                }
            }
            if(!complexBoolean) {
                break;
            }
        }
        Integer currentTemperature = (Integer) ((JSONObject) response.get("current")).get("temperature");
        assertEquals(true, currentTemperature > -90 && currentTemperature < 60 && complexBoolean);
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

    @Test
    void isThreeHourStep() {
        API api = new API();
        boolean complexBoolean = true;
        JSONObject response = api.getForecast("Tallinn");
        JSONObject nextThreeDays = (JSONObject) response.get("nextDays");
        for(String dayKey : nextThreeDays.keySet()) {
            JSONObject dayForecast = (JSONObject) nextThreeDays.get(dayKey);
            String previousKey = "";
            int i = 0;
            for(String hourKey : dayForecast.keySet()) { //assuming that hourKeys are 0, 3, 6, 9, 12, 15, 18, 21
                if (i != 0) { // ignoring first key
                    complexBoolean = Integer.parseInt(hourKey) - Integer.parseInt(previousKey) == 3;
                    if(!complexBoolean) {
                        break;
                    }
                } else {
                    i++;
                }
                previousKey = hourKey;
            }
            if(!complexBoolean) {
                break;
            }
        }
        assertEquals(true, complexBoolean);
    }

    @Test
    void isThreeDaysForecast() {
        API api = new API();
        boolean complexBoolean = true;
        JSONObject response = api.getForecast("Tallinn");
        JSONObject nextThreeDays = (JSONObject) response.get("nextDays");
        assertEquals(3, nextThreeDays.keySet().size());
    }

    @Test
    void isEveryDayForecastHasMinMaxTemp() {
        API api = new API();
        boolean complexBoolean = true;
        JSONObject response = api.getForecast("Tallinn");
        JSONObject nextThreeDays = (JSONObject) response.get("nextDays");
        for(String dayKey : nextThreeDays.keySet()) {
            JSONObject dayForecast = (JSONObject) nextThreeDays.get(dayKey);
            complexBoolean = dayForecast.has("minTemp") && dayForecast.has("maxTemp");
            if(!complexBoolean) {
                break;
            }
        }
        assertEquals(true, complexBoolean);
    }

    @Test
    void areMinMaxTempsDigit() {
        API api = new API();
        boolean complexBoolean = true;
        JSONObject response = api.getForecast("Tallinn");
        JSONObject nextThreeDays = (JSONObject) response.get("nextDays");
        for(String dayKey : nextThreeDays.keySet()) {
            JSONObject dayForecast = (JSONObject) nextThreeDays.get(dayKey);
            Integer minTemp = Integer.parseInt((String) dayForecast.get("minTemp"));
            Integer maxTemp = Integer.parseInt((String) dayForecast.get("maxTemp"));
            if(minTemp < -90 || minTemp > 60 || maxTemp < -90 || maxTemp > 60) {
                complexBoolean = false;
                break;
            }
        }
        assertEquals(true, complexBoolean);
    }

}
