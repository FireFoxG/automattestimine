package api;

import api.helpers.FileFormatHelper;
import api.helpers.TextFileReader;
import org.json.JSONObject;

import java.util.List;

import static api.Config.CURRENT_FORECAST_TAG;
import static api.Config.FIVE_DAYS_FORECAST_TAG;

public class ForecastReader {
    public JSONObject readCurrentForecastData(String cityName) {
        TextFileReader reader = new TextFileReader();
        List<String> data = reader.readDataFromFile(FileFormatHelper.addTxtIfNoFoundInFilename(cityName));
        String foundData = findDataByTag(data, CURRENT_FORECAST_TAG);
        return foundData.isEmpty() ? new JSONObject() : new JSONObject(foundData);
    }

    public JSONObject readFiveDayForecastData(String cityName) {
        TextFileReader reader = new TextFileReader();
        List<String> data = reader.readDataFromFile(FileFormatHelper.addTxtIfNoFoundInFilename(cityName));
        String foundData = findDataByTag(data, FIVE_DAYS_FORECAST_TAG);
        return foundData.isEmpty() ? new JSONObject() : new JSONObject(foundData);
    }

    public String findDataByTag(List<String> data, String tag) {
        for(String line : data) {
            if(line.startsWith(tag)) {
                return line.replace(tag,"");
            }
        }
        return "";
    }
}
