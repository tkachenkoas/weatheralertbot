package com.atstudio.volatileweatherbot.bot;

import com.atstudio.volatileweatherbot.processors.UpdateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class UpdateHandler {

    private final List<UpdateProcessor> processors;

    @Autowired
    public UpdateHandler(List<UpdateProcessor> processors) {
        this.processors = processors;
    }

    public void handle(Update update) {
        for (UpdateProcessor processor: processors) {
            if (processor.willTakeCareOf(update)) {
                return;
            }
        }
    }

}
