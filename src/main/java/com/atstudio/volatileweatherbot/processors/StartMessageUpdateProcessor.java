package com.atstudio.volatileweatherbot.processors;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.InitState;
import com.atstudio.volatileweatherbot.models.AlertInitDto;
import com.atstudio.volatileweatherbot.services.api.BotMessageProvider;
import com.atstudio.volatileweatherbot.services.api.AlertInitStateProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.atstudio.volatileweatherbot.services.UpdateFieldExtractor.*;
import static java.util.Arrays.asList;

@Component
public class StartMessageUpdateProcessor extends AbstractUpdateProcessor {

    private static List<String> INIT_SUBSCRIPTION_MESSAGES = asList("/start", "/subscribe");

    private final TgApiExecutor executor;
    private final BotMessageProvider messageSource;
    private final AlertInitStateProcessingService subscriptionCache;

    @Autowired
    public StartMessageUpdateProcessor(TgApiExecutor executor, BotMessageProvider messageSource, AlertInitStateProcessingService subscriptionCache) {
        this.executor = executor;
        this.messageSource = messageSource;
        this.subscriptionCache = subscriptionCache;
    }

    @Override
    protected void process(Update update) {
        subscriptionCache.storeForProcessing(AlertInitDto.builder()
                .chatId(getChatId(update))
                .state(InitState.CITY)
                .build());

        SendMessage aboutMessage = new SendMessage()
                .setChatId(getChatId(update))
                .setText(messageSource.getMessage("specify-city"));
        executor.execute(aboutMessage);
    }

    @Override
    protected boolean applicableFor(Update update) {
        return INIT_SUBSCRIPTION_MESSAGES.contains(getMessageText(update));
    }
}