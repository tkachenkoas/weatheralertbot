package com.atstudio.volatileweatherbot.processors;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartMessageUpdateProcessor extends AbstractUpdateProcessor {

    @Override
    protected void process(Update update) {

    }

    @Override
    protected boolean applicableFor(Update update) {
        return false;
    }
}
