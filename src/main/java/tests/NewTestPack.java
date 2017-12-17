package tests;

import api.NoDataFoundException;
import api.WeatherService;
import api.WeatherServiceImpl;
import api.forecasts.CurrentForecast;
import api.forecasts.FiveDayForecast;
import api.helpers.TextFileReader;
import api.helpers.TextFileWriter;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewTestPack {
    private static final String DEFAULT_CITY = "Tallinn";
    private static final String mockCurrentForecastJson = "{{\"coord\":{\"lon\":24.75,\"lat\":59.44},\"weather\":[{\"description\":\"overcast clouds\"}],\"main\":{\"temp\":25.20,\"temp_min\":10.10,\"temp_max\":28.15},\"sys\":{\"country\":\"EE\"},\"name\":\"Tallinn\",\"cod\":200}}";


    @Test
    void isCurrentTemperatureReal() throws NoDataFoundException{
        WeatherService api = new WeatherServiceImpl();
        CurrentForecast response = api.getForecast(DEFAULT_CITY);
        Double currentTemperature = response.getCurrentTemperature();
        assertEquals(true, currentTemperature > -90 && currentTemperature < 60 ? true : false); // minimum temperature recorded on Earth -89.2, maximum 56.7
    }

    @Test
    void isRightResponseCityInCurrentForecast() throws NoDataFoundException{
        WeatherService api = new WeatherServiceImpl();
        CurrentForecast response = api.getForecast(DEFAULT_CITY);
        assertEquals(DEFAULT_CITY, response.getCityName());
    }

    @Test
    void isRightResponseCityInFiveDayForecast() throws NoDataFoundException{
        WeatherService api = new WeatherServiceImpl();
        FiveDayForecast response = api.getFiveDayForecast(DEFAULT_CITY);
        assertEquals(DEFAULT_CITY, response.getCityName());
    }

    @Test
    void checkIfExternalApiIsAvailable() {
        WeatherService api = new WeatherServiceImpl();
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
        WeatherService api = new WeatherServiceImpl();
        CurrentForecast response = api.getForecast(DEFAULT_CITY);
        assertEquals("EE", response.getCountry());
    }

    @Test
    void checkIfFiveDaysForecastHasCountrySet() throws  NoDataFoundException{
        WeatherService api = new WeatherServiceImpl();
        FiveDayForecast response = api.getFiveDayForecast(DEFAULT_CITY);
        assertEquals("EE", response.getCountry());
    }

    @Test
    void getCurrentForecastWithMockAndTryReadingCoordinatesExperiment() throws NoDataFoundException {
        WeatherService weatherServiceMock = mock(WeatherService.class);
        when(weatherServiceMock.getForecast(anyString())).thenReturn(new CurrentForecast(new JSONObject(mockCurrentForecastJson)));
        CurrentForecast forecast = weatherServiceMock.getForecast("GG");
        assertEquals(59.44, forecast.getLatitude());

//          // create mock
//        WeatherRepository weatherRepositoryMock = mock(WeatherRepository.class);
//          // create class to test [given]
//        FileWriter writer = new FileWriter();
//          // stub the mock
//        when(weatherRepositoryMock.getCurrentWeather(any(WeatherRequest.class))).thenReturn(report);
//          // [when] invoke the test class
//        writer.writeToFileAsJson(weatherRepositoryMock.getCurrentWeather(request)," ...");
//            // [then] test the behaviour when after invocation
//        assertEquals(gson.toJson(report), reader.readFromFile("...", "output.txt"));

    }




}
