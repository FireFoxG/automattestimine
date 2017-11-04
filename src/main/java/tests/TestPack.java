package tests;

import api.WeatherService;
import api.WeatherServiceImpl;
import org.json.*;
import org.junit.jupiter.api.Test;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    private static final String DEFAULT_CITY = "Tallinn";

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

/* update 08.10.17
    So actual response from Weather service can be of two different types -
        1-day looks like this:
            {"coord":{"lon":139,"lat":35},
            "sys":{"country":"JP","sunrise":1369769524,"sunset":1369821049},
            "weather":[{"id":804,"main":"clouds","description":"overcast clouds","icon":"04n"}],
            "main":{"temp":289.5,"humidity":89,"pressure":1013,"temp_min":287.04,"temp_max":292.04},
            "wind":{"speed":7.31,"deg":187.002},
            "rain":{"3h":0},
            "clouds":{"all":92},
            "dt":1369824698,
            "id":1851632,
            "name":"Shuzenji",
            "cod":200}

        5-day looks like this:
            {"city":{"id":1851632,"name":"Shuzenji",
            "coord":{"lon":138.933334,"lat":34.966671},
            "country":"JP",
            "cod":"200",
            "message":0.0045,
            "cnt":38,
            "list":[
                {"dt":1406106000,
                "main":{
                    "temp":298.77,
                    "temp_min":298.77,
                    "temp_max":298.774,
                    "pressure":1005.93,
                    "sea_level":1018.18,
                    "grnd_level":1005.93,
                    "humidity":87
                    "temp_kf":0.26},
                "weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],
                "clouds":{"all":88},
                "wind":{"speed":5.71,"deg":229.501},
                "sys":{"pod":"d"},
                "dt_txt":"2014-07-23 09:00:00"}
                ]}

*/

    @Test
    void isResponseJson() {
        WeatherService api = new WeatherServiceImpl();
        JSONObject response = api.getForecast(DEFAULT_CITY);
        assertEquals(true, response.toString().startsWith("{") && response.toString().endsWith("}"));
    }

    @Test
    void isResponseValid() {
        WeatherService api = new WeatherServiceImpl();
        JSONObject response = api.getForecast(DEFAULT_CITY);
        assertEquals(true, response.has("cod") && response.get("cod").toString().equals("200"));
    }

    @Test
    void isCurrentTemperatureReal() {
        WeatherService api = new WeatherServiceImpl();
        JSONObject response = api.getForecast(DEFAULT_CITY);
        Integer currentTemperature = (Integer) ((JSONObject) response.get("main")).get("temp");
        assertEquals(true, currentTemperature > -90 && currentTemperature < 60 ? true : false); // minimum temperature recorded on Earth -89.2, maximum 56.7
    }

    @Test
    void isThreeDayForecastTemperatureReal() {
        WeatherService api = new WeatherServiceImpl();
        boolean complexBoolean =  true;
        JSONObject response = api.getFiveDayForecast(DEFAULT_CITY);
       JSONArray nextFiveDays = (JSONArray) response.get("list");
        int i = 0;
        for(Object nextThreeHourForecast : nextFiveDays) {
            Double hourTemperature = (Double) (((JSONObject) ((JSONObject) nextThreeHourForecast).get("main"))).get("temp");
            if(hourTemperature < -90 || hourTemperature > 60) {
                complexBoolean = false;
                break;
            }
        }
        assertEquals(true, complexBoolean);
    }

    @Test
    void isRightResponseCity() {
        WeatherService api = new WeatherServiceImpl();
        JSONObject response = api.getForecast(DEFAULT_CITY);
        assertEquals(DEFAULT_CITY, (String) response.get("name"));
    }

    @Test
    void isRightCoordinateFormat() {
        WeatherService api = new WeatherServiceImpl();
        JSONObject response = api.getForecast(DEFAULT_CITY);
        Pattern pattern = Pattern.compile("^\\d{3}$");
        assertEquals(true, ((JSONObject) response.get("coord")).get("lon") instanceof Double && ((JSONObject) response.get("coord")).get("lat") instanceof Double);
    }

    @Test
    void isForecastWithThreeHourStep() {
        WeatherService api = new WeatherServiceImpl();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        boolean complexBoolean = true;
        JSONObject response = api.getFiveDayForecast(DEFAULT_CITY);
        JSONArray nextFiveDays = (JSONArray) response.get("list");
        String previousTime = "";
        String thisTime = "";
        int i = 0;
        for(Object nextThreeHourForecast : nextFiveDays) {
            thisTime = (String) ((JSONObject) nextThreeHourForecast).get("dt_txt");
            if (i != 0) { // ignoring first key
                LocalDateTime  date1 = LocalDateTime .parse(thisTime, formatter);
                LocalDateTime  date2 = LocalDateTime.parse(previousTime, formatter);
                long hours = ChronoUnit.HOURS.between(date2, date1);
                complexBoolean = hours == 3;

                if (!complexBoolean) {
                    break;
                }
            } else {
                i++;
            }
            previousTime = thisTime;
        }
        assertEquals(true, complexBoolean);
    }

    @Test
    void isEveryDayForecastHasMinMaxTemp() {
        WeatherService api = new WeatherServiceImpl();
        boolean complexBoolean = true;
        JSONObject response = api.getFiveDayForecast(DEFAULT_CITY);
        JSONArray nextThreeDays = (JSONArray) response.get("list");
        for(Object nextThreeHours : nextThreeDays) {
            JSONObject nextThreeHoursForecast = (JSONObject) nextThreeHours;
            JSONObject nextThreeHoursForecastMain = (JSONObject) nextThreeHoursForecast.get("main");
            complexBoolean = nextThreeHoursForecastMain.has("temp_min") && nextThreeHoursForecastMain.has("temp_max");
            if(!complexBoolean) {
                break;
            }
        }
        assertEquals(true, complexBoolean);
    }

    @Test
    void areMinMaxTempsLegit() {
        WeatherService api = new WeatherServiceImpl();
        boolean complexBoolean = true;
        JSONObject response = api.getFiveDayForecast(DEFAULT_CITY);
        JSONArray nextFiveDays = (JSONArray) response.get("list");
        for(Object nextThreeHours : nextFiveDays) {
            JSONObject nextThreeHoursForecast = (JSONObject) nextThreeHours;
            Double minTemp = (Double) ((JSONObject) nextThreeHoursForecast.get("main")).get("temp_min");
            Double maxTemp = (Double) ((JSONObject) nextThreeHoursForecast.get("main")).get("temp_max");
            if(minTemp < -90 || minTemp > 60 || maxTemp < -90 || maxTemp > 60) {
                complexBoolean = false;
                break;
            }
        }
        assertEquals(true, complexBoolean);
    }
}
