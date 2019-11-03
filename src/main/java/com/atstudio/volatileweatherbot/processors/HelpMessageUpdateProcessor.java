package com.atstudio.volatileweatherbot.processors;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.common.BotMessageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

@Component
public class HelpMessageUpdateProcessor extends AbstractUpdateProcessor {

    private static String HELP_MESSAGE = "/help";
    private final TgApiExecutor executor;
    private final BotMessageProvider messageSource;

    @Autowired
    public HelpMessageUpdateProcessor(TgApiExecutor executor, BotMessageProvider messageSource) {
        this.executor = executor;
        this.messageSource = messageSource;
    }

    @Override
    protected void process(Update update) {
        SendMessage aboutMessage = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(messageSource.getMessage("about-bot"));
        executor.execute(aboutMessage);
    }

    @Override
    protected boolean applicableFor(Update update) {
        return equalsIgnoreCase(
                HELP_MESSAGE,
                ofNullable(update)
                        .map(Update::getMessage)
                        .map(Message::getText).orElse("")
        );
    }
}
