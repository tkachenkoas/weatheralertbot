package com.atstudio.volatileweatherbot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertInitDto {
    private Long chatId;
    private InitState state;

    private List<CityDto> matchedCities;
    private CityDto city;

    public void nextState() {
        this.setState(state.next());
    }
}