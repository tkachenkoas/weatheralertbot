package com.atstudio.volatileweatherbot.services.updateprocessors;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getChatId;

@Component
public class DefaultUpdateProcessor {

    private final TgApiExecutor executor;
    private final BotMessageProvider messageSource;

    @Autowired
    public DefaultUpdateProcessor(TgApiExecutor executor, BotMessageProvider messageSource) {
        this.executor = executor;
        this.messageSource = messageSource;
    }

    public void handle(Update update) {
        executor.execute(
                new SendMessage(getChatId(update), messageSource.getMessage("unknown-command"))
        );
    }

}
