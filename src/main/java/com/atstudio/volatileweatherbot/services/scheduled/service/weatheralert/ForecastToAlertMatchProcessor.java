package com.atstudio.volatileweatherbot.services.scheduled.service.weatheralert;

import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;

public interface ForecastToAlertMatchProcessor {

    /**
     * Checks whether given alert matches given forecast. If matches, will add required messages to ChatAlertContext.
     * It's callers responsibility to init and clear ChatAlertContext
     */
    void checkForecastForAlertMatch(WeatherAlert alert, WeatherForecast forecast);

}
