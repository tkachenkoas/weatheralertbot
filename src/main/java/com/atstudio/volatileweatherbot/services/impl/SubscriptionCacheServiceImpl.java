package com.atstudio.volatileweatherbot.services.impl;

import com.atstudio.volatileweatherbot.models.SubscriptionDto;
import com.atstudio.volatileweatherbot.services.api.SubscriptionCacheService;
import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionCacheServiceImpl implements SubscriptionCacheService {

    private final Cache<Integer, SubscriptionDto> subscriptionCache;

    @Autowired
    public SubscriptionCacheServiceImpl(Cache<Integer, SubscriptionDto> subscriptionCache) {
        this.subscriptionCache = subscriptionCache;
    }

    @Override
    public void save(SubscriptionDto dto) {
        subscriptionCache.put(dto.getUserId(), dto);
    }

    @Override
    public void remove(Integer userId) {
        subscriptionCache.invalidate(userId);
    }

    @Override
    public SubscriptionDto get(Integer userId) {
        return subscriptionCache.getIfPresent(userId);
    }
}