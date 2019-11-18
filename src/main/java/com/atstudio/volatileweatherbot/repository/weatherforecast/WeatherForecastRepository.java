package com.atstudio.volatileweatherbot.repository.weatherforecast;

import com.atstudio.volatileweatherbot.models.domain.Location;
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;

import java.time.LocalDateTime;

public interface WeatherForecastRepository {

    WeatherForecast storeForecast(WeatherForecast forecast);

    WeatherForecast getLocationForecastForLocalTime(Location location, LocalDateTime dateTime);

}
