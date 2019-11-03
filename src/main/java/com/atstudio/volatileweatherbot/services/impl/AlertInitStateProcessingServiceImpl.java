package com.atstudio.volatileweatherbot.services.impl;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.AlertInitDto;
import com.atstudio.volatileweatherbot.models.InitState;
import com.atstudio.volatileweatherbot.models.WeatherAlert;
import com.atstudio.volatileweatherbot.repository.AlertRepository;
import com.atstudio.volatileweatherbot.services.api.AlertInitStateProcessingService;
import com.atstudio.volatileweatherbot.services.api.BotMessageProvider;
import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class AlertInitStateProcessingServiceImpl implements AlertInitStateProcessingService {

    private final Cache<Long, AlertInitDto> cache;
    private final AlertRepository repository;
    private final TgApiExecutor executor;
    private final BotMessageProvider messageProvider;

    @Autowired
    public AlertInitStateProcessingServiceImpl(Cache<Long, AlertInitDto> cache,
                                               AlertRepository repository,
                                               TgApiExecutor executor,
                                               BotMessageProvider messageProvider) {
        this.cache = cache;
        this.repository = repository;
        this.executor = executor;
        this.messageProvider = messageProvider;
    }

    @Override
    public void storeForProcessing(AlertInitDto dto) {
        if (dto.getState() == null) {
            dto.setState(InitState.CITY);
        }
        if (dto.getState() == InitState.DONE) {
            executor.execute(
                    new SendMessage(
                            dto.getChatId(),
                            messageProvider.getMessage("alert-created")
                    )
            );
            repository.save(
                    WeatherAlert.builder()
                            .chatId(dto.getChatId())
                            .lat(dto.getCity().getLat())
                            .lng(dto.getCity().getLng())
                            .build()
            );
        } else {
            cache.put(dto.getChatId(), dto);
        }
    }

    @Override
    public AlertInitDto get(Long chatId) {
        return cache.getIfPresent(chatId);
    }
}