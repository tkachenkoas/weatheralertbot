package com.atstudio.volatileweatherbot.config;

import com.atstudio.volatileweatherbot.services.external.weather.OpenWeatherMapApiAccessor;
import com.atstudio.volatileweatherbot.services.external.weather.OpenWeatherMapApiAccessorImpl;
import com.google.maps.internal.ratelimiter.RateLimiter;
import org.openweathermap.api.DataWeatherClient;
import org.openweathermap.api.UrlConnectionDataWeatherClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.google.maps.internal.ratelimiter.RateLimiter.create;
import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${open-weather-map.api.key:}')")
@Profile("!integration-tests")
public class OpenWeatherMapApiConfig {

    @Bean
    protected DataWeatherClient client(@Value("${open-weather-map.api.key}") String apiKey) {
        return new UrlConnectionDataWeatherClient(apiKey);
    }

    /**
     * This is to ensure that api won't be called more than <i>maxRpm</i> times per minute (free account allows 60)
     */
    @Bean
    protected RateLimiter perMinuteRateLimiter(@Value("${open-weather-map.max.rpm:30}") Integer maxRpm) {
        return create(1.0, maxRpm, SECONDS);
    }

    @Bean
    public OpenWeatherMapApiAccessor apiAccessor(DataWeatherClient client, RateLimiter rateLimiter) {
        return new OpenWeatherMapApiAccessorImpl(client, rateLimiter);
    }

}