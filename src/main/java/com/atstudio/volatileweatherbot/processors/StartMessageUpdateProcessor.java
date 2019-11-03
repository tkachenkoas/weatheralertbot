package com.atstudio.volatileweatherbot.processors;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.InitState;
import com.atstudio.volatileweatherbot.models.SubscriptionDto;
import com.atstudio.volatileweatherbot.services.BotMessageProvider;
import com.atstudio.volatileweatherbot.services.SubscriptionCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.atstudio.volatileweatherbot.services.UpdateFieldExtractor.*;
import static java.util.Arrays.asList;

@Component
public class StartMessageUpdateProcessor extends AbstractUpdateProcessor {

    private static List<String> MESSAGES = asList("/start", "/subscribe");

    private final TgApiExecutor executor;
    private final BotMessageProvider messageSource;
    private final SubscriptionCacheService subscriptionCache;

    @Autowired
    public StartMessageUpdateProcessor(TgApiExecutor executor, BotMessageProvider messageSource, SubscriptionCacheService subscriptionCache) {
        this.executor = executor;
        this.messageSource = messageSource;
        this.subscriptionCache = subscriptionCache;
    }

    @Override
    protected void process(Update update) {
        subscriptionCache.save(SubscriptionDto.builder()
                .userId(getUserId(update))
                .state(InitState.CITY)
                .build());

        SendMessage aboutMessage = new SendMessage()
                .setChatId(getChatId(update))
                .setText(messageSource.getMessage("specify-city"));
        executor.execute(aboutMessage);
    }

    @Override
    protected boolean applicableFor(Update update) {
        return MESSAGES.contains(getMessageText(update));
    }
}