package com.atstudio.volatileweatherbot.repository.alert;

import com.atstudio.volatileweatherbot.models.WeatherAlert;

public interface AlertRepository {

    WeatherAlert save(WeatherAlert alert);

}