package com.atstudio.volatileweatherbot.config;

import com.atstudio.volatileweatherbot.services.external.geo.googlemaps.GoogleApiAccessor;
import com.atstudio.volatileweatherbot.services.external.geo.googlemaps.GoogleApiAccessorContextImpl;
import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${google.api.key:}')")
@Profile("!integration-tests")
public class GoogleApiConfig {

    @Bean
    public GeoApiContext geoApiContext(@Value("${google.api.key}") String apiKey) {
        return new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    @Bean
    public GoogleApiAccessor contextApiAccessor(GeoApiContext geoApiContext) {
        return new GoogleApiAccessorContextImpl(geoApiContext);
    }

}