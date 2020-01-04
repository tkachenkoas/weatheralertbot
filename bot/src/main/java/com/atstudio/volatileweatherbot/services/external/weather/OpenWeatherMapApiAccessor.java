package com.atstudio.volatileweatherbot.services.external.weather;

import com.atstudio.volatileweatherbot.models.domain.Location;
import org.openweathermap.api.model.forecast.ForecastInformation;
import org.openweathermap.api.model.forecast.hourly.HourlyForecast;

public interface OpenWeatherMapApiAccessor {

    ForecastInformation<HourlyForecast> getHourlyForecast(Location location);

}
