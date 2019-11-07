package com.atstudio.volatileweatherbot.services.updateprocessors.initalert;

import com.atstudio.volatileweatherbot.models.dto.AlertInitDto;
import com.atstudio.volatileweatherbot.models.dto.StagePhase;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractInitStageProcessor implements InitStageProcessor {

    @Override
    public AlertInitDto process(Update update, AlertInitDto currentInitState) {
        return currentInitState.getPhase() == StagePhase.STARTED
                ? startPhase(update, currentInitState)
                : processingPhase(update, currentInitState);
    }

    protected AlertInitDto onProcessingPhase(AlertInitDto initDto) {
        initDto.setPhase(StagePhase.PROCESSING);
        return initDto;
    }

    protected AlertInitDto doneProcessing(AlertInitDto initDto) {
        initDto.setPhase(StagePhase.DONE);
        return initDto;
    }

    protected abstract AlertInitDto startPhase(Update update, AlertInitDto initDto);

    protected abstract AlertInitDto processingPhase(Update update, AlertInitDto initDto);

}
