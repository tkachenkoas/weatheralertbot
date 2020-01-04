package com.atstudio.volatileweatherbot.services.util;

public interface BotMessageProvider {

    String getMessage(String code);
    String getMessageWithArgs(String code, Object... args);

}
