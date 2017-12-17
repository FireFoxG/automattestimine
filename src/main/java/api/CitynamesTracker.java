package api;

import api.helpers.TextFileReader;
import api.helpers.TextFileWriter;

import java.util.List;

import static api.Config.INPUT_FILENAME;

public class CitynamesTracker {
    public void writeCityname(String cityname) {
        if(!cityname.isEmpty()) {
            TextFileReader reader = new TextFileReader();
            List<String> data = reader.readDataFromFile(INPUT_FILENAME);
            for(String line : data) {
                if(line.equals(cityname)) {
                    return;
                }
            }
            TextFileWriter writer = new TextFileWriter();
            writer.appendDataToFile(cityname, INPUT_FILENAME);
        }
    }
}
