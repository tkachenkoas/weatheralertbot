package com.atstudio.volatileweatherbot.repository.weatheralert;

import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;

import java.util.List;

public interface AlertRepository {

    void save(WeatherAlert alert);
    List<WeatherAlert> getForLocation(String locationCode);

}