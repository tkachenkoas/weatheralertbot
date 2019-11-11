package com.atstudio.volatileweatherbot.models.domain.forecast;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RainInfo {
    private BigDecimal expectedAmount;
    private String description;
}
