package com.atstudio.volatileweatherbot.models.dto;

import lombok.*;

import java.util.List;

@Data
public class AlertInitDto {
    private final Long chatId;

    private List<CityDto> matchedCities;
    private CityDto city;

    private InitStage stage;
    private StagePhase phase;

    public AlertInitDto(Long chatId) {
        this.chatId = chatId;
        nextStage();
    }

    public void nextStage() {
        this.stage = InitStage.next(stage);
        this.phase = StagePhase.STARTED;
    }
}