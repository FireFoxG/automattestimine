package api;

import api.helpers.TextFileReader;
import api.helpers.TextFileWriter;

import java.util.List;

import static api.Config.INPUT_FILENAME;

public class CitynamesTracker {
    private TextFileReader reader;
    private TextFileWriter writer;

    public CitynamesTracker(TextFileReader reader, TextFileWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public void writeCityname(String cityname) {
        if(!cityname.isEmpty()) {
            TextFileReader reader = new TextFileReader();
            List<String> data = reader.readDataFromFile(INPUT_FILENAME);
            for(String line : data) {
                if(line.trim().toLowerCase().equals(cityname.trim().toLowerCase())) {
                    return;
                }
            }
            TextFileWriter writer = new TextFileWriter();
            writer.appendDataToFile(cityname.trim(), INPUT_FILENAME);
        }
    }
}
