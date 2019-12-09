package com.atstudio.volatileweatherbot.services.updateprocessors.subscriptionlist;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.repository.weatheralert.AlertRepository;
import com.atstudio.volatileweatherbot.services.updateprocessors.AbstractUpdateProcessor;
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getChatId;
import static java.util.Optional.ofNullable;

@Component
@Slf4j
public class RemoveAlertUpdateProcessor extends AbstractUpdateProcessor {

    static final String REMOVE_PREFIX = "RMV_ALRT_";
    private final TgApiExecutor executor;
    private final BotMessageProvider messageSource;
    private final AlertRepository alertRepository;

    @Autowired
    public RemoveAlertUpdateProcessor(TgApiExecutor executor, BotMessageProvider messageSource, AlertRepository alertRepository) {
        this.executor = executor;
        this.messageSource = messageSource;
        this.alertRepository = alertRepository;
    }

    @Override
    protected void process(Update update) {
        CallbackQuery callback = update.getCallbackQuery();
        String alertUuid = callback.getData().replace(REMOVE_PREFIX, "");
        if (alertRepository.removeByUuid(alertUuid)) {
            executor.execute(
                    new SendMessage(getChatId(update), messageSource.getMessage("alert-removed"))
            );
        } else {
            log.warn("Wasn't able to remove alert with uuid {} from update callback {}", alertUuid, update);
        };
    }

    @Override
    protected boolean applicableFor(Update update) {
        return ofNullable(update.getCallbackQuery())
                .map(CallbackQuery::getData)
                .map(data -> data.startsWith(REMOVE_PREFIX))
                .orElse(false);
    }

}
