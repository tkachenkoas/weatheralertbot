package com.atstudio.volatileweatherbot.services.scheduled;

import com.atstudio.volatileweatherbot.repository.location.LocationRepository;
import com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherForecastRefresher {

    private final LocationRepository locationRepository;
    private final WeatherForecastProvider weatherForecastProvider;
    private final WeatherForecastRepository weatherForecastRepository;

    @Autowired
    public WeatherForecastRefresher(LocationRepository locationRepository,
                                    WeatherForecastProvider weatherForecastProvider,
                                    WeatherForecastRepository weatherForecastRepository) {
        this.locationRepository = locationRepository;
        this.weatherForecastProvider = weatherForecastProvider;
        this.weatherForecastRepository = weatherForecastRepository;
    }

    @Scheduled(fixedRateString = "${scheduled.forecast-refresh.delay}")
    void refreshWeatherForecast() {

    }

}
