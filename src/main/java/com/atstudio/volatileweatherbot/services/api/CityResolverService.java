package com.atstudio.volatileweatherbot.services.api;

import com.atstudio.volatileweatherbot.models.CityDto;

import java.util.List;

public interface CityResolverService {

    List<CityDto> getCities(String city);
}