package com.atstudio.volatileweatherbot.services.external.geo.googlemaps;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

public class GoogleApiAccessorContextImpl implements GoogleApiAccessor {

    private final GeoApiContext context;

    @Autowired
    public GoogleApiAccessorContextImpl(GeoApiContext context) {
        this.context = context;
    }

    @Override
    @SneakyThrows
    public GeocodingResult[] getGeocodings(String citySearch) {
        return GeocodingApi.geocode(context, citySearch).await();
    }
}