package com.atstudio.volatileweatherbot.services.api;

public interface BotMessageProvider {

    String getMessage(String code);
    String getMessage(String code, Object[] args);

}
