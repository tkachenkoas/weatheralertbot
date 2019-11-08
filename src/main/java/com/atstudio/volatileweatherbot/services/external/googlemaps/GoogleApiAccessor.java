package com.atstudio.volatileweatherbot.services.external.googlemaps;

import com.google.maps.model.GeocodingResult;

public interface GoogleApiAccessor {

    GeocodingResult[] getGeocodings(String citySearch); 
    
}
