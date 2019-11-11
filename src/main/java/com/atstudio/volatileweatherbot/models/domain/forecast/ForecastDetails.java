package com.atstudio.volatileweatherbot.models.domain.forecast;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ForecastDetails {

    private BigDecimal temperature;
    private BigDecimal temperatureDeviation;
    private RainInfo rainInfo;

}
