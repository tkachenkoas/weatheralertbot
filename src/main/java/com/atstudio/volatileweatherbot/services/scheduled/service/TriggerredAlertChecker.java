package com.atstudio.volatileweatherbot.services.scheduled.service;

import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;
import com.atstudio.volatileweatherbot.repository.weatheralert.AlertRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TriggerredAlertChecker {

    private final AlertRepository alertRepository;

    public TriggerredAlertChecker(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public void checkTriggeredAlerts() {
        List<WeatherAlert> upcomingAlerts = alertRepository.getTriggeredAlerts();
    }

}
