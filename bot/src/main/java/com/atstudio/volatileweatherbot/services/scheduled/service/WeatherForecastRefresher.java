package com.atstudio.volatileweatherbot.services.scheduled.service;

import com.atstudio.volatileweatherbot.models.domain.Location;
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;
import com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastRepository;
import com.atstudio.volatileweatherbot.services.external.weather.WeatherForecastProvider;
import com.atstudio.volatileweatherbot.services.scheduled.dao.ForecastRefreshDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class WeatherForecastRefresher {

    private final ForecastRefreshDao forecastRefreshDao;
    private final WeatherForecastProvider weatherForecastProvider;
    private final WeatherForecastRepository weatherForecastRepository;
    private final TaskExecutor asyncExecutor;

    @Autowired
    public WeatherForecastRefresher(ForecastRefreshDao forecastRefreshDao,
                                    WeatherForecastProvider weatherForecastProvider,
                                    WeatherForecastRepository weatherForecastRepository,
                                    @Qualifier("async") TaskExecutor taskExecutor) {
        this.forecastRefreshDao = forecastRefreshDao;
        this.weatherForecastProvider = weatherForecastProvider;
        this.weatherForecastRepository = weatherForecastRepository;
        this.asyncExecutor = taskExecutor;
    }

    @Scheduled(fixedRateString = "${scheduled.forecast-refresh.delay}")
    public void refreshWeatherForecast() {
        Set<Location> locations = forecastRefreshDao.getLocationsForForecastRefresh();
        log.debug("Started forecast refresh job. Locations for refresh: {}", locations.size());
        if (locations.isEmpty()) {
            return;
        }
        locations.forEach(location -> asyncExecutor.execute(() -> {
            WeatherForecast forLocation = weatherForecastProvider.getClosestForecastForLocation(location);
            weatherForecastRepository.storeForecast(forLocation);
        }));

    }

}