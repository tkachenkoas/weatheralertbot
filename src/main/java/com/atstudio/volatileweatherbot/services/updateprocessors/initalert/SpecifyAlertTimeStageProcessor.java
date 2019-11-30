package com.atstudio.volatileweatherbot.services.updateprocessors.initalert;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.dto.AlertInitDto;
import com.atstudio.volatileweatherbot.models.dto.InitStage;
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getMessageText;

@Service
public class SpecifyAlertTimeStageProcessor extends AbstractInitStageProcessor {

    private final BotMessageProvider messageSource;
    private final TgApiExecutor executor;

    public SpecifyAlertTimeStageProcessor(BotMessageProvider messageSource,
                                          TgApiExecutor executor) {
        this.messageSource = messageSource;
        this.executor = executor;
    }

    @Override
    public InitStage applicableForStage() {
        return InitStage.SPECIFY_TIME;
    }

    @Override
    protected AlertInitDto startPhase(Update update, AlertInitDto initDto) {
        executor.execute(
                new SendMessage(
                        initDto.getChatId(),
                        messageSource.getMessage("specify-time")
                )
        );
        return onProcessingPhase(initDto);
    }

    @Override
    protected AlertInitDto processingPhase(Update update, AlertInitDto initDto) {
        String timeString = getMessageText(update);
        // will turn 8:00 into 08:00
        if (timeString.indexOf(":") == 1) {
            timeString = "0" + timeString;
        }
        try {
            initDto.setAlertLocalTime(LocalTime.parse(timeString));
            return doneProcessing(initDto);
        } catch (DateTimeParseException e) {
            executor.execute(
                    new SendMessage(
                            initDto.getChatId(),
                            messageSource.getMessageWithArgs("bad-time-format", timeString)
                    )
            );
            return onProcessingPhase(initDto);
        }
    }

}
