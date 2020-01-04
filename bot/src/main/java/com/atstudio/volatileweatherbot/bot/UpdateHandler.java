package com.atstudio.volatileweatherbot.bot;

import com.atstudio.volatileweatherbot.aspect.LogArgsAndResult;
import com.atstudio.volatileweatherbot.services.updateprocessors.DefaultUpdateProcessor;
import com.atstudio.volatileweatherbot.services.updateprocessors.UpdateProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@Slf4j
public class UpdateHandler {

    private final List<UpdateProcessor> processors;
    private final DefaultUpdateProcessor defaultUpdateProcessor;

    @Autowired
    public UpdateHandler(List<UpdateProcessor> processors, DefaultUpdateProcessor defaultUpdateProcessor) {
        this.processors = processors;
        this.defaultUpdateProcessor = defaultUpdateProcessor;
    }

    @LogArgsAndResult
    public void handle(Update update) {
        for (UpdateProcessor processor: processors) {
            if (processor.willTakeCareOf(update)) {
                return;
            }
        }
        defaultUpdateProcessor.handle(update);
    }

}
