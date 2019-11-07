package com.atstudio.volatileweatherbot.services.external;

import com.atstudio.volatileweatherbot.models.dto.CityDto;

import java.util.List;

public interface CityResolverService {

    List<CityDto> getCities(String city);
}