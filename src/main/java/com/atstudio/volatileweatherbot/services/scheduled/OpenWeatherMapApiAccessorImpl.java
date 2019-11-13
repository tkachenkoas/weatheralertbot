package com.atstudio.volatileweatherbot.services.scheduled;

import com.atstudio.volatileweatherbot.aspect.LogArgsAndResult;
import com.atstudio.volatileweatherbot.models.domain.Location;
import com.google.maps.internal.ratelimiter.RateLimiter;
import org.openweathermap.api.DataWeatherClient;
import org.openweathermap.api.common.Coordinate;
import org.openweathermap.api.model.currentweather.CurrentWeather;
import org.openweathermap.api.query.Language;
import org.openweathermap.api.query.QueryBuilderPicker;
import org.openweathermap.api.query.ResponseFormat;
import org.openweathermap.api.query.UnitFormat;
import org.openweathermap.api.query.currentweather.CurrentWeatherOneLocationQuery;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenWeatherMapApiAccessorImpl implements OpenWeatherMapApiAccessor {

    private final DataWeatherClient client;
    private final RateLimiter rateLimiter;

    @Autowired
    public OpenWeatherMapApiAccessorImpl(DataWeatherClient client, RateLimiter rateLimiter) {
        this.client = client;
        this.rateLimiter = rateLimiter;
    }

    @Override
    @LogArgsAndResult
    public CurrentWeather getCurrentWeather(Location location) {
        this.rateLimiter.acquire();
        CurrentWeatherOneLocationQuery query = QueryBuilderPicker.pick()
                .currentWeather()
                .oneLocation()
                .byGeographicCoordinates(
                        new Coordinate(
                                location.getLng().toPlainString(),
                                location.getLat().toPlainString()
                        )
                )
                .language(Language.ENGLISH)
                .responseFormat(ResponseFormat.JSON)
                .unitFormat(UnitFormat.METRIC)
                .build();
        return client.getCurrentWeather(query);
    }
}
