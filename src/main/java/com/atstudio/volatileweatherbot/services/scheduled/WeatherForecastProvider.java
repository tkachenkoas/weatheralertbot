package com.atstudio.volatileweatherbot.services.scheduled;

import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;
import com.atstudio.volatileweatherbot.models.domain.Location;

public interface WeatherForecastProvider {

    WeatherForecast getClosestForecastForLocation(Location location);

}
