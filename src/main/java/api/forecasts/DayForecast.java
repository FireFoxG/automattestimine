package api.forecasts;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class DayForecast {

    private List<TimeForecast> timeForecasts;
    private LocalDate date;

    public DayForecast(List<TimeForecast> timeForecasts) {
        this.timeForecasts = timeForecasts;
        if(timeForecasts.size() > 0) {
            this.date = timeForecasts.get(0).time.toLocalDate();
        }
    }

    public double getMaximumTemperatureForTheDay() {
        OptionalDouble optionalDouble = timeForecasts.stream().mapToDouble(t -> t.getMaxTemperature()).max();
        return optionalDouble.isPresent() ? optionalDouble.getAsDouble() : 999;
    }

    public double getMinimumTemperatureForTheDay() {
        OptionalDouble optionalDouble = timeForecasts.stream().mapToDouble(t -> t.getMinTemperature()).min();
        return optionalDouble.isPresent() ? optionalDouble.getAsDouble() : -999;
    }

    public LocalDate getDate() {
        return date;
    }

    public String toString() {
        return getDate() + "\n"
                + " Max temp: " + getMaximumTemperatureForTheDay() + "\n"
                + " Min temp: " + getMinimumTemperatureForTheDay() + "\n";
    }
}
