package com.atstudio.volatileweatherbot.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CityDto {
    private String cityId;
    private String displayedName;
}