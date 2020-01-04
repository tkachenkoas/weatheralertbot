package com.atstudio.volatileweatherbot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class VolatileWeatherBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUserName;
    @Value("${bot.token}")
    private String botToken;

    private final UpdateHandler handler;

    private final TaskExecutor updateExecutor;

    @Autowired
    public VolatileWeatherBot(UpdateHandler handler, @Qualifier("update") TaskExecutor updateExecutor) {
        this.handler = handler;
        this.updateExecutor = updateExecutor;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateExecutor.execute(() -> handler.handle(update));
    }

}
