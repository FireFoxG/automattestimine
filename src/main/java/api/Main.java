package api;

import api.forecasts.CurrentForecast;
import api.forecasts.FiveDayForecast;
import api.helpers.JsonReceiver;
import api.helpers.TextFileReader;
import api.helpers.TextFileWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        WeatherService service = new WeatherServiceImpl(
                new ForecastReader(new TextFileReader()),
                new JsonReceiver(),
                new TextFileWriter(),
                new TextFileReader());
        service.updateLocalData();
        try {
            CurrentForecast forecast = service.getForecast("New York");
            System.out.println(forecast);
        } catch (NoDataFoundException e) {
            e.printStackTrace();
        }
        System.out.println();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                System.out.print("Enter name of the city: ");
                String s = br.readLine();
                if(s.trim().length() > 0) {
                    try {
                        CurrentForecast forecast1 = service.getForecast(s);
                        System.out.println(forecast1);

                        FiveDayForecast forecast2 = service.getFiveDayForecast(s);
                        System.out.println(forecast2);
                        System.out.println("Maximum temperature in next 3 days is: " + forecast2.getMaxTempForNextThreeDays());
                        System.out.println("Minimum temp in next 3 days is: " + forecast2.getMinTempForNextThreeDays());

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