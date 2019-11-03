package com.atstudio.volatileweatherbot.services.impl;

import com.atstudio.volatileweatherbot.models.SubscriptionDto;
import com.atstudio.volatileweatherbot.services.api.SubscriptionCacheService;
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
        subscriptionCache.put(dto.getChatId(), dto);
    }

    @Override
    public void remove(Long chatId) {
        subscriptionCache.invalidate(chatId);
    }

    @Override
    public SubscriptionDto get(Long chatId) {
        return subscriptionCache.getIfPresent(chatId);
    }
}