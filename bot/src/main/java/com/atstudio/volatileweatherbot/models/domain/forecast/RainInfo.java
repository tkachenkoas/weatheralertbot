package com.atstudio.volatileweatherbot.models.domain.forecast;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RainInfo {
    private BigDecimal expectedAmount;
    private String description;
}
