package com.atstudio.volatileweatherbot.bot;

import com.atstudio.volatileweatherbot.processors.UpdateProcessor;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@Slf4j
public class UpdateHandler {

    private final List<UpdateProcessor> processors;
    private final Gson gson;

    @Autowired
    public UpdateHandler(List<UpdateProcessor> processors, Gson gson) {
        this.processors = processors;
        this.gson = gson;
    }

    public void handle(Update update) {
        log.info("Incoming update: {}", gson.toJson(update));
        for (UpdateProcessor processor: processors) {
            if (processor.willTakeCareOf(update)) {
                return;
            }
        }
    }

}
