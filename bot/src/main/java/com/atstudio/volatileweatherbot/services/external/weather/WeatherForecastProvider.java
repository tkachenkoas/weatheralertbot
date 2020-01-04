package com.atstudio.volatileweatherbot.services.external.weather;

import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;
import com.atstudio.volatileweatherbot.models.domain.Location;

public interface WeatherForecastProvider {

    WeatherForecast getClosestForecastForLocation(Location location);

}
