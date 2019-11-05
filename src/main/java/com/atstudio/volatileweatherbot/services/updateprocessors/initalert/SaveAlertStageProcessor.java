package com.atstudio.volatileweatherbot.services.updateprocessors.initalert;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.AlertInitDto;
import com.atstudio.volatileweatherbot.models.InitStage;
import com.atstudio.volatileweatherbot.models.WeatherAlert;
import com.atstudio.volatileweatherbot.repository.alert.AlertRepository;
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class SaveAlertStageProcessor extends AbstractInitStageProcessor {

    private final AlertRepository repository;
    private final TgApiExecutor executor;
    private final BotMessageProvider messageProvider;

    @Autowired
    public SaveAlertStageProcessor(AlertRepository repository, TgApiExecutor executor, BotMessageProvider messageProvider) {
        this.repository = repository;
        this.executor = executor;
        this.messageProvider = messageProvider;
    }

    @Override
    protected AlertInitDto startPhase(Update update, AlertInitDto initDto) {
        return processingPhase(update, initDto);
    }

    @Override
    protected AlertInitDto processingPhase(Update update, AlertInitDto initDto) {
        executor.execute(
                new SendMessage(
                        initDto.getChatId(),
                        messageProvider.getMessage("alert-created")
                )
        );
        repository.save(
                WeatherAlert.builder()
                        .chatId(initDto.getChatId())
                        .cityCode(initDto.getCity().getCityCode())
                        .lat(initDto.getCity().getLat())
                        .lng(initDto.getCity().getLng())
                        .build()
        );
        return doneProcessing(initDto);
    }

    @Override
    public InitStage applicableForStage() {
        return InitStage.READY_TO_SAVE;
    }
}
