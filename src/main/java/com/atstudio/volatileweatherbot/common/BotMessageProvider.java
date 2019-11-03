package com.atstudio.volatileweatherbot.common;

public interface BotMessageProvider {

    String getMessage(String code);
    String getMessage(String code, Object[] args);

}
