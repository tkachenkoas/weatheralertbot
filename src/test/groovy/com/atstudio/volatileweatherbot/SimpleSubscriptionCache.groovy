package com.atstudio.volatileweatherbot

import com.atstudio.volatileweatherbot.models.SubscriptionDto
import com.atstudio.volatileweatherbot.services.SubscriptionCacheService

class SimpleSubscriptionCache implements SubscriptionCacheService {

    Map<Integer, SubscriptionDto> cache = new HashMap<>();

    @Override
    void save(SubscriptionDto dto) {
        cache.put(dto.getUserId(), dto)
    }

    @Override
    void remove(Integer userId) {
        cache.remove(userId)
    }

    @Override
    SubscriptionDto get(Integer userId) {
        return cache.get(userId)
    }
}
