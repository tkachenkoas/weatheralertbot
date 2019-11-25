package com.atstudio.volatileweatherbot.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherAlert {
    private String uuid;
    private Long chatId;
    private AlertWeatherType alertWeatherType;
    private String locationLabel;
    private String locationCode;
    private LocalTime localAlertTime;
}