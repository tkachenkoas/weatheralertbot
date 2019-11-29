package com.atstudio.volatileweatherbot.repository.weatherforecast;

import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;

import java.time.LocalDateTime;

public interface WeatherForecastRepository {

    WeatherForecast storeForecast(WeatherForecast forecast);
    WeatherForecast getLatestForecastForLocation(String locationCode);
    WeatherForecast getLatestLocationForecastForLocalTime(String locationCode, LocalDateTime dateTime);

}
