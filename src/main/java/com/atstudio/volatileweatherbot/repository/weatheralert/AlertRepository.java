package com.atstudio.volatileweatherbot.repository.weatheralert;

import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;

import java.util.List;

public interface AlertRepository {

    WeatherAlert save(WeatherAlert alert);

    boolean removeByUuid(String uuid);

    List<WeatherAlert> getAlertsForChatId(Long chatId);

    List<WeatherAlert> getTriggeredAlerts();

    void postponeAlertsForTomorrow(List<WeatherAlert> alert);
}