package com.atstudio.volatileweatherbot.models.domain.forecast;

import com.atstudio.volatileweatherbot.models.domain.AlertWeatherType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ForecastDetails {

    private LocalDateTime targetDateTime;
    private AlertWeatherType expectedWeatherType;
    private BigDecimal temperature;
    private BigDecimal temperatureDeviation;
    private RainInfo rainInfo;

}
