package com.atstudio.volatileweatherbot.services.updateprocessors.initalert;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.domain.Location;
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;
import com.atstudio.volatileweatherbot.models.dto.AlertInitDto;
import com.atstudio.volatileweatherbot.models.dto.CityDto;
import com.atstudio.volatileweatherbot.models.dto.InitStage;
import com.atstudio.volatileweatherbot.repository.location.LocationRepository;
import com.atstudio.volatileweatherbot.repository.weatheralert.AlertRepository;
import com.atstudio.volatileweatherbot.services.external.geo.TimeZoneResolver;
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalTime;

import static java.util.Collections.singletonList;

@Service
public class SaveAlertStageProcessor extends AbstractInitStageProcessor {

    private final AlertRepository alertRepository;
    private final LocationRepository locationRepository;
    private final TgApiExecutor executor;
    private final BotMessageProvider messageProvider;
    private final TimeZoneResolver timeZoneResolver;

    @Autowired
    public SaveAlertStageProcessor(AlertRepository repository,
                                   LocationRepository locationRepository,
                                   TgApiExecutor executor,
                                   BotMessageProvider messageProvider,
                                   TimeZoneResolver timeZoneResolver) {
        this.alertRepository = repository;
        this.locationRepository = locationRepository;
        this.executor = executor;
        this.messageProvider = messageProvider;
        this.timeZoneResolver = timeZoneResolver;
    }

    @Override
    protected AlertInitDto startPhase(Update update, AlertInitDto initDto) {
        return processingPhase(update, initDto);
    }

    @Override
    @Transactional
    protected AlertInitDto processingPhase(Update update, AlertInitDto initDto) {
        Location location = toLocation(initDto.getCity());
        locationRepository.createIfNotExists(location);
        WeatherAlert alert = alertRepository.save(toWeatherAlert(initDto));
        // If an alert is created for approximately current time, it'll have it's chance
        boolean alertTimeHasPassed = LocalTime.now(location.getTimeZone()).minusMinutes(5)
                .isAfter(alert.getLocalAlertTime());
        if (alertTimeHasPassed) {
            alertRepository.postponeAlertForTomorrow(singletonList(alert));
        }
        executor.execute(
                new SendMessage(
                        initDto.getChatId(),
                        messageProvider.getMessage("alert-created")
                )
        );
        return doneProcessing(initDto);
    }

    @Override
    public InitStage applicableForStage() {
        return InitStage.READY_TO_SAVE;
    }

    private WeatherAlert toWeatherAlert(AlertInitDto initDto) {
        return WeatherAlert.builder()
                .chatId(initDto.getChatId())
                .locationCode(initDto.getCity().getCode())
                .localAlertTime(initDto.getAlertLocalTime())
                .weatherType(initDto.getWeatherType())
                .locationLabel(initDto.getCity().getShortName())
                .build();
    }

    private Location toLocation(CityDto cityDto) {
        return new Location(
                cityDto.getCode(),
                cityDto.getLat(),
                cityDto.getLng(),
                timeZoneResolver.timeZoneForCoordinates(cityDto.getLat(), cityDto.getLng())
        );
    }
}
