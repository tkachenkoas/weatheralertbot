package com.atstudio.volatileweatherbot.services.external.geo.googlemaps;

import com.atstudio.volatileweatherbot.services.external.geo.TimeZoneResolver;
import com.google.maps.GeoApiContext;
import com.google.maps.TimeZoneApi;
import com.google.maps.model.LatLng;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.TimeZone;

public class GoogleApiTimeZoneResolver implements TimeZoneResolver {

    private final GeoApiContext context;

    public GoogleApiTimeZoneResolver(GeoApiContext context) {
        this.context = context;
    }

    @Override
    @SneakyThrows
    public ZoneId timeZoneForCoordinates(BigDecimal lat, BigDecimal lng) {
        TimeZone timeZone = TimeZoneApi.getTimeZone(context, new LatLng(lat.doubleValue(), lng.doubleValue())).await();
        return timeZone.toZoneId();
    }
}
