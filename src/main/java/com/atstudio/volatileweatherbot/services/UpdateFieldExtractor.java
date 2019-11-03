package com.atstudio.volatileweatherbot.services;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static java.util.Optional.ofNullable;

public class UpdateFieldExtractor {

    public static String getMessageText(Update update) {
        return ofNullable(update)
                .map(Update::getMessage)
                .map(Message::getText).orElse("");
    }

    public static Integer getUserId(Update update) {
        return ofNullable(update)
                .map(Update::getMessage)
                .map(Message::getFrom)
                .map(User::getId).orElse(null);
    }

    public static Long getChatId(Update update) {
        return ofNullable(update)
                .map(Update::getMessage)
                .map(Message::getChatId)
                .orElse(null);
    }

}
