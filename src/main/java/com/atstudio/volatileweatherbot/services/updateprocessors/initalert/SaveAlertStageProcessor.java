package com.atstudio.volatileweatherbot.services.updateprocessors.initalert;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.dto.AlertInitDto;
import com.atstudio.volatileweatherbot.models.dto.InitStage;
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;
import com.atstudio.volatileweatherbot.repository.weatheralert.AlertRepository;
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class SaveAlertStageProcessor extends AbstractInitStageProcessor {

    private final AlertRepository alertRepository;
    private final TgApiExecutor executor;
    private final BotMessageProvider messageProvider;

    @Autowired
    public SaveAlertStageProcessor(AlertRepository repository, TgApiExecutor executor, BotMessageProvider messageProvider) {
        this.alertRepository = repository;
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
        alertRepository.save(toWeatherAlert(initDto));
        return doneProcessing(initDto);
    }

    @Override
    public InitStage applicableForStage() {
        return InitStage.READY_TO_SAVE;
    }

    private WeatherAlert toWeatherAlert(AlertInitDto initDto) {
        return WeatherAlert.builder()
                .chatId(initDto.getChatId())
                .locationCode(initDto.getCity().getCode())
                .alertWeatherType(initDto.getAlertWeatherType())
                .locationLabel(initDto.getCity().getShortName())
                .build();
    }
}
