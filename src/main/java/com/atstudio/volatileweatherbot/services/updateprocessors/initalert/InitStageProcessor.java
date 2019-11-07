package com.atstudio.volatileweatherbot.services.updateprocessors.initalert;

import com.atstudio.volatileweatherbot.models.dto.AlertInitDto;
import com.atstudio.volatileweatherbot.models.dto.InitStage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface InitStageProcessor {
    InitStage applicableForStage();
    AlertInitDto process(Update update, AlertInitDto currentInitState);
}
