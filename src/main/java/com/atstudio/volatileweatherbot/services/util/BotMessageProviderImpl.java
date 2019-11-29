package com.atstudio.volatileweatherbot.services.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class BotMessageProviderImpl implements BotMessageProvider {

    private final MessageSource messageSource;

    @Autowired
    public BotMessageProviderImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(String code) {
        return getMessage(code);
    }

    @Override
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.ENGLISH);
    }
}
