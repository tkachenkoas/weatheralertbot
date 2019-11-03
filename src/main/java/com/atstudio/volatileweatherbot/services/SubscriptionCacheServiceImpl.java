package com.atstudio.volatileweatherbot.services;

import com.atstudio.volatileweatherbot.models.SubscriptionDto;
import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionCacheServiceImpl implements SubscriptionCacheService {

    private final Cache<Long, SubscriptionDto> subscriptionCache;

    @Autowired
    public SubscriptionCacheServiceImpl(Cache<Long, SubscriptionDto> subscriptionCache) {
        this.subscriptionCache = subscriptionCache;
    }

    @Override
    public void save(SubscriptionDto dto) {

    }

    @Override
    public void remove(Integer userId) {

    }

    @Override
    public SubscriptionDto get(Integer userId) {
        return null;
    }
}