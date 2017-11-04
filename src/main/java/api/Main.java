package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        WeatherService service = new WeatherServiceImpl();
        FileWriter writer = new FileWriter();
        System.out.println();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                System.out.print("Enter name of the city: ");
                String s = br.readLine();
                if(s.trim().length() > 0) {
                    writer.writeStringToFile(s, "input.txt");
                    service.getForecast(s);
                    break;
                }
            }

            try{
                int i = Integer.parseInt(br.readLine());
            } catch(NumberFormatException nfe){
                System.err.println("Invalid Format!");
            }
        } catch (IOException e) {
            System.err.println("Invalid input");
        }
    }
}