package com.atstudio.volatileweatherbot.services;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

public class UpdateFieldExtractor {

    public static String getMessageText(Update update) {
        return getMessage(update).getText();
    }

    public static Long getChatId(Update update) {
        return getMessage(update).getChatId();
    }

    private static Message getMessage(Update update) {
        return ofNullable(update)
                .map(Update::getMessage)
                .orElseGet(
                        () -> ofNullable(update)
                                .map(Update::getCallbackQuery)
                                .map(CallbackQuery::getMessage)
                                .orElseThrow(cantHandle())
                );
    }

    private static Supplier<NullPointerException> cantHandle() {
        return () -> new NullPointerException("Could not extract required field from update");
    }

}
