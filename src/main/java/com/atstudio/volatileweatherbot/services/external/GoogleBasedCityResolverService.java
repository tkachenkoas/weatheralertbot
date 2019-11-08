package com.atstudio.volatileweatherbot.services.external;

import com.atstudio.volatileweatherbot.models.dto.CityDto;
import com.atstudio.volatileweatherbot.services.external.googlemaps.GoogleApiAccessor;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.util.Arrays.asList;

@Service
@Slf4j
public class GoogleBasedCityResolverService implements CityResolverService {

    private final GoogleApiAccessor apiAccessor;

    @Autowired
    public GoogleBasedCityResolverService(GoogleApiAccessor apiAccessor) {
        this.apiAccessor = apiAccessor;
    }

    @Override
    public List<CityDto> getCities(String city) {
        GeocodingResult[] geocodings = apiAccessor.getGeocodings(city);
        log.info("Got geocodings for city '{}' request: {}", city, asList(geocodings));
        return Stream.of(geocodings).map(this::mapToCityDto).collect(toList());
    }

    private CityDto mapToCityDto(GeocodingResult gc) {
        CityDto result = new CityDto();
        result.setCode(gc.placeId);
        result.setShortName(gc.addressComponents[0].shortName);
        result.setFullName(gc.formattedAddress);
        LatLng latLng = gc.geometry.location;
        result.setLat(
                new BigDecimal(latLng.lat)
                        .setScale(6, BigDecimal.ROUND_DOWN)
        );
        result.setLng(
                new BigDecimal(latLng.lng)
                        .setScale(6, BigDecimal.ROUND_DOWN)
        );
        return result;
    }
}
