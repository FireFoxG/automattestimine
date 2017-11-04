package api;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeForecast extends CurrentForecast {
    LocalDateTime time;

    public TimeForecast(JSONObject json) {
        super(json);
        String dateTimeString = getDateTimeStringFromJson(json);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.time = LocalDateTime.parse(dateTimeString, formatter);
    }

    private String getDateTimeStringFromJson(JSONObject json) {
        return json.has("dt_txt") ? json.getString("dt_txt") : "";
    }
}
