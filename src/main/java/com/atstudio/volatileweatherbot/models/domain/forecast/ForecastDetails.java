package com.atstudio.volatileweatherbot.models.domain.forecast;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ForecastDetails {

    private BigDecimal temperature;
    private BigDecimal temperatureDeviation;
    private RainInfo rainInfo;
    private LocalDateTime from;
    private LocalDateTime to;

}
