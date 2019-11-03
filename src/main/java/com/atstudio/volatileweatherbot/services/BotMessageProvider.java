package com.atstudio.volatileweatherbot.services;

public interface BotMessageProvider {

    String getMessage(String code);
    String getMessage(String code, Object[] args);

}
