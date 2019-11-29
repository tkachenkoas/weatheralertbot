package com.atstudio.volatileweatherbot.services.scheduled.service;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;
import com.atstudio.volatileweatherbot.repository.weatheralert.AlertRepository;
import com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastRepository;
import com.atstudio.volatileweatherbot.services.scheduled.service.weatheralert.ChatAlertContext;
import com.atstudio.volatileweatherbot.services.scheduled.service.weatheralert.ForecastToAlertMatchProcessor;
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

@Component
public class TriggeredAlertChecker {

    private final AlertRepository alertRepository;
    private final WeatherForecastRepository forecastRepository;
    private final List<ForecastToAlertMatchProcessor> matchProcessors;
    private final TgApiExecutor executor;
    private final BotMessageProvider messageProvider;

    public TriggeredAlertChecker(AlertRepository alertRepository,
                                 WeatherForecastRepository forecastRepository,
                                 List<ForecastToAlertMatchProcessor> matchProcessors,
                                 TgApiExecutor executor,
                                 BotMessageProvider messageProvider) {
        this.alertRepository = alertRepository;
        this.forecastRepository = forecastRepository;
        this.matchProcessors = matchProcessors;
        this.executor = executor;
        this.messageProvider = messageProvider;
    }

    @Scheduled(fixedRateString = "${scheduled.check-triggered.delay}")
    public void checkTriggeredAlerts() {
        List<WeatherAlert> upcomingAlerts = alertRepository.getTriggeredAlerts();

        Map<String, List<WeatherAlert>> groupedByLocationAndLocalTime = upcomingAlerts.stream()
                .collect(groupingBy(WeatherAlert::getLocationCode));

        try {
            ChatAlertContext.init();
            for (Map.Entry<String, List<WeatherAlert>> locationCodeToAlertListEntry : groupedByLocationAndLocalTime.entrySet()) {
                WeatherForecast forecast = forecastRepository.getLatestForecastForLocation(locationCodeToAlertListEntry.getKey());
                locationCodeToAlertListEntry.getValue().forEach(alert ->
                        matchProcessors.forEach(inf ->
                                inf.checkForecastForAlertMatch(alert, forecast)
                        )
                );
            }
            Map<Long, Map<String, List<String>>> chatMessageMap = ChatAlertContext.getResultChatMessageMap();
            for(Map.Entry<Long, Map<String, List<String>>> chatIdToMessageInfoEntry: chatMessageMap.entrySet()) {
                for (Map.Entry<String, List<String>> locationCodeToMessagesEntry: chatIdToMessageInfoEntry.getValue().entrySet()) {
                    String messageHeader = messageProvider.getMessage("alert-triggered", locationCodeToMessagesEntry.getKey());
                    String messageToSend = Stream.concat(Stream.of(messageHeader), locationCodeToMessagesEntry.getValue().stream())
                            .collect(joining("\n"));
                    executor.execute(
                            new SendMessage(
                                    chatIdToMessageInfoEntry.getKey(),
                                    messageToSend
                            )
                    );
                }
            }
            } finally {
            ChatAlertContext.clear();
        }
    }

}
