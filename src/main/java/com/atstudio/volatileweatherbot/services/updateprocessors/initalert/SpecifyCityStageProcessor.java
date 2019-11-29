package com.atstudio.volatileweatherbot.services.updateprocessors.initalert;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.dto.AlertInitDto;
import com.atstudio.volatileweatherbot.models.dto.CityDto;
import com.atstudio.volatileweatherbot.models.dto.InitStage;
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider;
import com.atstudio.volatileweatherbot.services.external.geo.CityResolverService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;

import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getChatId;
import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getMessageText;
import static java.util.stream.Collectors.toList;

@Service
public class SpecifyCityStageProcessor extends AbstractInitStageProcessor {

    private final BotMessageProvider messageSource;
    private final CityResolverService cityRevolver;
    private final TgApiExecutor executor;

    public SpecifyCityStageProcessor(BotMessageProvider messageSource,
                                     CityResolverService cityRevolver,
                                     TgApiExecutor executor) {
        this.messageSource = messageSource;
        this.cityRevolver = cityRevolver;
        this.executor = executor;
    }

    @Override
    public InitStage applicableForStage() {
        return InitStage.SPECIFY_CITY;
    }

    @Override
    protected AlertInitDto startPhase(Update update, AlertInitDto initDto) {
        executor.execute(
                new SendMessage(
                        initDto.getChatId(),
                        messageSource.getMessage("specify-city")
                )
        );
        return onProcessingPhase(initDto);
    }

    @Override
    protected AlertInitDto processingPhase(Update update, AlertInitDto initDto) {
        CallbackQuery callback = update.getCallbackQuery();
        if (callback != null) {
            return resolveCityFromCallback(initDto, callback);
        }

        String city = getMessageText(update);
        List<CityDto> matchedCities = cityRevolver.getCities(city);
        if (matchedCities.size() == 1) {
            return resolveCityForDto(initDto, matchedCities.get(0));
        }

        initDto.setMatchedCities(matchedCities);
        executor.execute(
                new SendMessage(
                        getChatId(update), messageSource.getMessage("city-guess", city)
                ).setReplyMarkup(getReplyMarkup(matchedCities))
        );
        return onProcessingPhase(initDto);
    }

    private AlertInitDto resolveCityFromCallback(AlertInitDto dto, CallbackQuery callback) {
        CityDto city = dto.getMatchedCities().stream()
                .filter(cityDto -> cityDto.getCode().equals(callback.getData()))
                .findFirst().orElseThrow(() -> new IllegalStateException("City not found by provided code!"));
        return resolveCityForDto(dto, city);
    }

    private AlertInitDto resolveCityForDto(AlertInitDto alertInitDto, CityDto city) {
        alertInitDto.setCity(city);
        return doneProcessing(alertInitDto);
    }

    private ReplyKeyboard getReplyMarkup(List<CityDto> guessedCities) {
        return new InlineKeyboardMarkup()
                .setKeyboard(
                        guessedCities.stream().map(
                                (cityDto) -> Collections.singletonList(
                                        new InlineKeyboardButton()
                                                .setText(cityDto.getFullName())
                                                .setCallbackData(cityDto.getCode())
                                )
                        ).collect(toList())
                );
    }

}
