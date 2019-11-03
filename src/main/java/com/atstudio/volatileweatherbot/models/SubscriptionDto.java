package com.atstudio.volatileweatherbot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SubscriptionDto {
    private Integer userId;
    private InitState state;
    private String cityCode;
}