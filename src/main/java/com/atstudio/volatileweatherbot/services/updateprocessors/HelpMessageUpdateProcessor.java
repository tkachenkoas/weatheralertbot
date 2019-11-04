package com.atstudio.volatileweatherbot.services.updateprocessors;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getChatId;
import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getMessageText;
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
                .setChatId(getChatId(update))
                .setText(messageSource.getMessage("about-bot"));
        executor.execute(aboutMessage);
    }

    @Override
    protected boolean applicableFor(Update update) {
        return equalsIgnoreCase(HELP_MESSAGE, getMessageText(update));
    }
}
