package api;

import api.forecasts.CurrentForecast;
import api.forecasts.FiveDayForecast;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        WeatherService service = new WeatherServiceImpl();
        TextFileWriter writer = new TextFileWriter();
        System.out.println();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                System.out.print("Enter name of the city: ");
                String s = br.readLine();
                if(s.trim().length() > 0) {
                    writer.writeStringToFile(s, "input.txt");
                    try {
                        CurrentForecast forecast1 = service.getForecast();
                        System.out.println(forecast1.getCityName() + " " + forecast1.getCountry());
                        System.out.println("Current temperature is: " + forecast1.getCurrentTemperature());
                        System.out.println("Description: " + forecast1.getWeatherDescription());

                        FiveDayForecast forecast2 = service.getFiveDayForecast();
                        System.out.println(forecast1.getCityName() + " " + forecast2.getCountry());
                        System.out.println("Maximum temperature is: " + forecast2.getMaxTempForNextThreeDays());
                        System.out.println("Minimum temp is: " + forecast2.getMinTempForNextThreeDays());

                    } catch (NoDataFoundException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Invalid input");
        }
    }
}