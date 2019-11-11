package com.atstudio.volatileweatherbot.models.domain.forecast;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WeatherForecast {
    private String locationCode;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private List<ForecastDetails> details;
}
