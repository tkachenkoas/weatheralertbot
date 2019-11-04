package com.atstudio.volatileweatherbot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherAlert {
    private Long chatId;
    private String cityCode;
    private BigDecimal lat;
    private BigDecimal lng;
}