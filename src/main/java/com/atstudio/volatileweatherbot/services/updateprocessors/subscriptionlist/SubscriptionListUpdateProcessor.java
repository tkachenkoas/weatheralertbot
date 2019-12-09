package com.atstudio.volatileweatherbot.services.updateprocessors.subscriptionlist;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;
import com.atstudio.volatileweatherbot.repository.weatheralert.AlertRepository;
import com.atstudio.volatileweatherbot.services.updateprocessors.AbstractUpdateProcessor;
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;

import static com.atstudio.volatileweatherbot.services.updateprocessors.subscriptionlist.RemoveAlertUpdateProcessor.REMOVE_PREFIX;
import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getChatId;
import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getMessageText;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

@Component
public class SubscriptionListUpdateProcessor extends AbstractUpdateProcessor {

    private static final String ALERTS_LIST = "/alerts";
    private final TgApiExecutor executor;
    private final BotMessageProvider messageSource;
    private final AlertRepository alertRepository;

    @Autowired
    public SubscriptionListUpdateProcessor(TgApiExecutor executor, BotMessageProvider messageSource, AlertRepository alertRepository) {
        this.executor = executor;
        this.messageSource = messageSource;
        this.alertRepository = alertRepository;
    }

    @Override
    protected void process(Update update) {
        Long chatId = getChatId(update);
        List<WeatherAlert> alertsForChatId = alertRepository.getAlertsForChatId(chatId);
        if (alertsForChatId.isEmpty()) {
            executor.execute(new SendMessage(chatId, messageSource.getMessage("no-active-subscriptions")));
            return;
        }

        SendMessage alertsListMessage = new SendMessage(chatId, messageSource.getMessage("active-subscriptions"))
                .setReplyMarkup(getReplyMarkup(alertsForChatId));
        executor.execute(alertsListMessage);
    }

    @Override
    protected boolean applicableFor(Update update) {
        return equalsIgnoreCase(ALERTS_LIST, getMessageText(update));
    }

    private ReplyKeyboard getReplyMarkup(List<WeatherAlert> alerts) {
        return new InlineKeyboardMarkup()
                .setKeyboard(
                        alerts.stream().map(
                                (alert) -> Collections.singletonList(
                                        new InlineKeyboardButton()
                                                .setText(alertText(alert))
                                                .setCallbackData(REMOVE_PREFIX + alert.getUuid())
                                )
                        ).collect(toList())
                );
    }

    private String alertText(WeatherAlert alert) {
        return messageSource.getMessageWithArgs("alert-list-button",
                alert.getLocationLabel(),
                alert.getLocalAlertTime(),
                messageSource.getMessage("weather_type_" + alert.getWeatherType().name().toLowerCase()).toLowerCase()
        );
    }

}
