package com.atstudio.volatileweatherbot.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherAlert {
    private Long chatId;
    private AlertWeatherType alertWeatherType;
    private String locationLabel;
    private String locationCode;
}