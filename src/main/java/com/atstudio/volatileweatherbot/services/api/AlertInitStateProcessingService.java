package com.atstudio.volatileweatherbot.services.api;

import com.atstudio.volatileweatherbot.models.AlertInitDto;

public interface AlertInitStateProcessingService {

    void storeForProcessing(AlertInitDto dto);
    AlertInitDto get(Long chatId);

}