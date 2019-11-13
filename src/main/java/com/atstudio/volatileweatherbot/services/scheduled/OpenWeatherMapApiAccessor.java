package com.atstudio.volatileweatherbot.services.scheduled;

import com.atstudio.volatileweatherbot.models.domain.Location;
import org.openweathermap.api.model.currentweather.CurrentWeather;

public interface OpenWeatherMapApiAccessor {

    CurrentWeather getCurrentWeather(Location location);

}
