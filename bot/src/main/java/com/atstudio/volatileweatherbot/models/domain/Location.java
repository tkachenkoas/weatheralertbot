package com.atstudio.volatileweatherbot.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZoneId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
    private String code;
    private BigDecimal lat;
    private BigDecimal lng;
    private ZoneId timeZone;
}
