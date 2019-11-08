package com.atstudio.volatileweatherbot.services.external.googlemaps;

import com.google.maps.model.GeocodingResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("integration-tests")
public class MockGoogleApiAccessor implements GoogleApiAccessor {

    @Override
    public GeocodingResult[] getGeocodings(String citySearch) {
        return new GeocodingResult[0];
    }
}
