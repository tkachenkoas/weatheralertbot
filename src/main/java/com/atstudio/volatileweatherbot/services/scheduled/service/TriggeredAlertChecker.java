package com.atstudio.volatileweatherbot.services.scheduled.service;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;
import com.atstudio.volatileweatherbot.repository.weatheralert.AlertRepository;
import com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastRepository;
import com.atstudio.volatileweatherbot.services.scheduled.service.weatheralert.ChatAlertContext;
import com.atstudio.volatileweatherbot.services.scheduled.service.weatheralert.ForecastToAlertMatchProcessor;
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

@Component
@Slf4j
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
        log.debug("Started triggered alert checker job. Upcoming alerts count: {}", upcomingAlerts.size());
        if (upcomingAlerts.isEmpty()) {
            return;
        }

        Map<String, List<WeatherAlert>> groupedByLocationAndLocalTime = upcomingAlerts.stream()
                .collect(groupingBy(WeatherAlert::getLocationCode));

        List<WeatherAlert> alertsToPostpone = new ArrayList<>();
        try {
            ChatAlertContext.init();
            for (Map.Entry<String, List<WeatherAlert>> locationCodeToAlertListEntry : groupedByLocationAndLocalTime.entrySet()) {
                WeatherForecast forecast = forecastRepository.getLatestForecastForLocation(locationCodeToAlertListEntry.getKey());
                if (forecast == null) {
                    log.warn("No recent forecast available for location {}", locationCodeToAlertListEntry.getKey());
                    continue;
                }

                List<WeatherAlert> locationAlerts = locationCodeToAlertListEntry.getValue();
                locationAlerts.forEach(alert ->
                        matchProcessors.forEach(inf ->
                                inf.checkCurrentForecastForAlertMatch(alert, forecast)
                        )
                );
                alertsToPostpone.addAll(locationAlerts);
            }

            Map<Long, Map<String, List<String>>> chatMessageMap = ChatAlertContext.getResultChatMessageMap();
            for (Map.Entry<Long, Map<String, List<String>>> chatIdToMessageInfoEntry : chatMessageMap.entrySet()) {
                for (Map.Entry<String, List<String>> locationCodeToMessagesEntry : chatIdToMessageInfoEntry.getValue().entrySet()) {
                    String messageHeader = messageProvider.getMessageWithArgs("alert-triggered", locationCodeToMessagesEntry.getKey());
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
            alertRepository.postponeAlertForTomorrow(alertsToPostpone);
        } finally {
            ChatAlertContext.clear();
        }
    }

}
