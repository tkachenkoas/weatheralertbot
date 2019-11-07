package com.atstudio.volatileweatherbot.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class Location {
    private String code;
    private BigDecimal lat;
    private BigDecimal lng;
}
