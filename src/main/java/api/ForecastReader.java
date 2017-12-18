package api;

import api.helpers.FileFormatHelper;
import api.helpers.TextFileReader;
import org.json.JSONObject;

import javax.xml.soap.Text;
import java.util.List;

import static api.Config.CURRENT_FORECAST_TAG;
import static api.Config.FIVE_DAYS_FORECAST_TAG;

public class ForecastReader {
    private TextFileReader reader;

    public ForecastReader(TextFileReader reader) {
        this.reader = reader;
    }

    public JSONObject readForecastDataByTag(String cityName, WeatherServiceImpl.ForecastType type) {
        List<String> data = reader.readDataFromFile(FileFormatHelper.addTxtIfNoFoundInFilename(cityName));
        String foundData = type.equals(WeatherServiceImpl.ForecastType.CURRENT) ? findDataByTag(data, CURRENT_FORECAST_TAG) : findDataByTag(data, FIVE_DAYS_FORECAST_TAG);
        return foundData.isEmpty() ? new JSONObject() : new JSONObject(foundData);
    }

    public JSONObject readCurrentForecastData(String cityName) {
        return readForecastDataByTag(cityName, WeatherServiceImpl.ForecastType.CURRENT);
    }

    public JSONObject readFiveDayForecastData(String cityName) {
        return readForecastDataByTag(cityName, WeatherServiceImpl.ForecastType.FIVEDAYS);
    }

    public String findDataByTag(List<String> data, String tag) {
        for(String line : data) {
            if(line.startsWith(tag)) {
                return line.replace(tag,"");
            }
        }
        return "";
    }

    public TextFileReader getTextFileReader() {
        return reader;
    }
}
