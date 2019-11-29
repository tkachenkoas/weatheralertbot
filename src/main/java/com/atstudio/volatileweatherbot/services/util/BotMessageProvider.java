package com.atstudio.volatileweatherbot.services.util;

public interface BotMessageProvider {

    String getMessage(String code);
    String getMessage(String code, Object... args);

}
