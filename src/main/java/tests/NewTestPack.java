package tests;

import api.*;
import api.forecasts.CurrentForecast;
import api.forecasts.ThreeDaysForecast;
import api.helpers.JsonReceiver;
import api.helpers.TextFileReader;
import api.helpers.TextFileWriter;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static api.Config.INPUT_FILENAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class NewTestPack {
    private static final String DEFAULT_CITY = "Tallinn";
    private static final String mockCurrentForecastJson = "{{\"coord\":{\"lon\":24.75,\"lat\":59.44},\"weather\":[{\"description\":\"overcast clouds\"}],\"main\":{\"temp\":25.20,\"temp_min\":10.10,\"temp_max\":28.15},\"sys\":{\"country\":\"EE\"},\"name\":\"Tallinn\",\"cod\":200}}";

    public WeatherService api;
    public TextFileReader textFileReader;
    public ForecastReader forecastReader;
    public CitynamesTracker tracker;
    public JsonReceiver receiver;
    public TextFileWriter writer;

    @BeforeEach
    void setup(){
        textFileReader = new TextFileReader();
        forecastReader = new ForecastReader(textFileReader);
        receiver = new JsonReceiver();
        writer = new TextFileWriter();
        api = new WeatherServiceImpl(forecastReader, receiver, writer, textFileReader);

    }

    @Test
    void isCurrentTemperatureReal() throws NoDataFoundException{
        CurrentForecast response = api.getForecast(DEFAULT_CITY);
        Double currentTemperature = response.getCurrentTemperature();
        assertEquals(true, currentTemperature > -90 && currentTemperature < 60 ? true : false); // minimum temperature recorded on Earth -89.2, maximum 56.7
    }

    @Test
    void isRightResponseCityInCurrentForecast() throws NoDataFoundException{
        CurrentForecast response = api.getForecast(DEFAULT_CITY);
        assertEquals(DEFAULT_CITY, response.getCityName());
    }

    @Test
    void isRightResponseCityInFiveDayForecast() throws NoDataFoundException{
        ThreeDaysForecast response = api.getFiveDayForecast(DEFAULT_CITY);
        assertEquals(DEFAULT_CITY, response.getCityName());
    }

    @Test
    void checkIfExternalApiIsAvailable() {
        assertTrue(api.serviceIsAvailable());
    }

    @Test
    void checkIfWritingToFileWorks() throws IOException {
        String testFilename = "test1";
        File file = new File(testFilename);
        boolean result = Files.deleteIfExists(file.toPath()); //surround it in try catch block
        TextFileWriter writer = new TextFileWriter();
        writer.writeDataToFile("testdata",testFilename);
        file = new File(testFilename);
        assertTrue(file.exists());
    }
    @Test
    void checkIfReadingFromFileWorks() {
        String testFilename = "test2";
        String testData = "123321^gfdgfdgd";
        TextFileWriter writer = new TextFileWriter();
        writer.writeDataToFile(testData,testFilename);
        TextFileReader reader = new TextFileReader();
        List<String> data = reader.readDataFromFile(testFilename);
        for(String line : data) {
            if(line.contains(testData)) {
                assertTrue(true);
                return;
            }
        }
        assertTrue(false);
    }

    @Test
    void checkIfCurrentForecastHasCountrySet() throws  NoDataFoundException{
        CurrentForecast response = api.getForecast(DEFAULT_CITY);
        assertEquals("EE", response.getCountry());
    }

    @Test
    void checkIfFiveDaysForecastHasCountrySet() throws  NoDataFoundException{
        ThreeDaysForecast response = api.getFiveDayForecast(DEFAULT_CITY);
        assertEquals("EE", response.getCountry());
    }

    @Test
    void checkIfCitynamesTrackerReadsData() {
        TextFileReader textFileReader = mock(TextFileReader.class);
        ForecastReader reader = new ForecastReader(textFileReader);
        JsonReceiver jsonReceiver = new JsonReceiver();
        WeatherService api2 = new WeatherServiceImpl(reader, jsonReceiver, writer, textFileReader);
        api2.updateLocalData();
        verify(textFileReader).readDataFromFile(INPUT_FILENAME);
    }

    @Test
    void checkIfgetForecastTriesToReadDataIfNoConnecton() throws NoDataFoundException, IOException{
        TextFileReader textFileReader = mock(TextFileReader.class);
        ForecastReader reader = new ForecastReader(textFileReader);
        JsonReceiver jsonReceiver = mock(JsonReceiver.class);
        WeatherService api2 = new WeatherServiceImpl(reader,jsonReceiver, writer, textFileReader);
        when(jsonReceiver.readJsonFromUrl(anyString())).thenReturn(new JSONObject());
        api2.getForecast(DEFAULT_CITY);
        verify(textFileReader).readDataFromFile(DEFAULT_CITY+".txt");
    }

    @Test
    void checkIfUpdateMethodTriesToReadFromExternalSourceTwice() throws IOException {
        TextFileReader textFileReader = mock(TextFileReader.class);
        ForecastReader reader = new ForecastReader(textFileReader);
        JsonReceiver jsonReceiver = mock(JsonReceiver.class);
        TextFileWriter writer = mock(TextFileWriter.class);
        when(textFileReader.readDataFromFile(INPUT_FILENAME)).thenReturn(Arrays.asList("Berlin\nOslo"));
        when(jsonReceiver.readJsonFromUrl(anyString())).thenReturn(new JSONObject());
        WeatherService api2 = new WeatherServiceImpl(reader,jsonReceiver, writer, textFileReader);
        api2.updateLocalData();
        verify(jsonReceiver, times(2)).readJsonFromUrl(anyString());
    }




}
