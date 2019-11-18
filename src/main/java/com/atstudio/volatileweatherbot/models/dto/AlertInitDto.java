package com.atstudio.volatileweatherbot.models.dto;

import com.atstudio.volatileweatherbot.models.domain.AlertWeatherType;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class AlertInitDto {
    private final Long chatId;

    private List<CityDto> matchedCities;
    private CityDto city;

    private AlertWeatherType alertWeatherType;
    private LocalTime alertLocalTime;

    private InitStage stage;
    private StagePhase phase;

    public AlertInitDto(Long chatId) {
        alertLocalTime = LocalTime.of(8, 0);
        this.chatId = chatId;
        this.alertWeatherType = AlertWeatherType.RAIN;
        nextStage();
    }

    public void nextStage() {
        this.stage = InitStage.next(stage);
        this.phase = StagePhase.STARTED;
    }
}