package com.atstudio.volatileweatherbot.services.updateprocessors;

import com.atstudio.volatileweatherbot.models.AlertInitDto;
import com.atstudio.volatileweatherbot.models.InitStage;
import com.atstudio.volatileweatherbot.models.StagePhase;
import com.atstudio.volatileweatherbot.services.updateprocessors.initalert.InitStageProcessor;
import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getChatId;
import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getMessageText;
import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Service
public class InitAlertUpdateProcessor implements UpdateProcessor {

    private static List<String> INIT_ALERT_MESSAGES = asList("/start", "/subscribe");

    private final Cache<Long, AlertInitDto> cache;
    private final Map<InitStage, InitStageProcessor> stageProcessors;

    @Autowired
    public InitAlertUpdateProcessor(Cache<Long, AlertInitDto> cache, List<InitStageProcessor> processors) {
        this.cache = cache;
        this.stageProcessors = processors.stream()
                .collect(toMap(InitStageProcessor::applicableForStage, identity()));
    }

    @Override
    public boolean willTakeCareOf(Update update) {
        Long chatId = getChatId(update);
        if (INIT_ALERT_MESSAGES.contains(getMessageText(update))) {
            AlertInitDto dto = new AlertInitDto(chatId);
            cache.put(chatId, dto);
        }
        AlertInitDto initDtoForChat = cache.getIfPresent(chatId);
        if (initDtoForChat == null) {
            return false;
        }
        processCurrentStageForUpdate(update, initDtoForChat);
        return true;
    }

    private void processCurrentStageForUpdate(Update update, AlertInitDto initDto) {
        if (initDto.getStage() == InitStage.CREATED) {
            cache.invalidate(initDto.getChatId());
            return;
        }
        InitStageProcessor processor = stageProcessors.get(initDto.getStage());
        AlertInitDto processed = processor.process(update, initDto);
        if (processed.getPhase() == StagePhase.DONE) {
            processed.nextStage();
            processCurrentStageForUpdate(update, processed);
        }
    }
}
