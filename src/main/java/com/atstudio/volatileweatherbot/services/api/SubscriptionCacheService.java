package com.atstudio.volatileweatherbot.services.api;

import com.atstudio.volatileweatherbot.models.SubscriptionDto;

public interface SubscriptionCacheService {

    void save(SubscriptionDto dto);
    void remove(Long chatId);
    SubscriptionDto get(Long chatId);

}