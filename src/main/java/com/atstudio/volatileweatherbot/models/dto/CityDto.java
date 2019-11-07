package com.atstudio.volatileweatherbot.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityDto {
    private String code;
    private String displayedName;
    private BigDecimal lat;
    private BigDecimal lng;
}