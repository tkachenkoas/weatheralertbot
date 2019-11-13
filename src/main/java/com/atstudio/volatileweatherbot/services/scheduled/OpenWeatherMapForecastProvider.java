package com.atstudio.volatileweatherbot.services.scheduled;

import com.atstudio.volatileweatherbot.models.domain.Location;
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;
import org.openweathermap.api.model.currentweather.CurrentWeather;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenWeatherMapForecastProvider implements WeatherForecastProvider {

    private final OpenWeatherMapApiAccessor apiAccessor;

    @Autowired
    public OpenWeatherMapForecastProvider(OpenWeatherMapApiAccessor apiAccessor) {
        this.apiAccessor = apiAccessor;
    }

    @Override
    public WeatherForecast getClosestForecastForLocation(Location location) {
        CurrentWeather currentWeather = apiAccessor.getCurrentWeather(location);
        WeatherForecast forecast = new WeatherForecast();

        return forecast;
    }
}
