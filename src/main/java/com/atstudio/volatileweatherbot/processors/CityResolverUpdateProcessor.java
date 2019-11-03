package com.atstudio.volatileweatherbot.processors;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.AlertInitDto;
import com.atstudio.volatileweatherbot.models.CityDto;
import com.atstudio.volatileweatherbot.models.InitState;
import com.atstudio.volatileweatherbot.services.api.AlertInitStateProcessingService;
import com.atstudio.volatileweatherbot.services.api.BotMessageProvider;
import com.atstudio.volatileweatherbot.services.api.CityResolverService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;

import static com.atstudio.volatileweatherbot.services.UpdateFieldExtractor.getChatId;
import static com.atstudio.volatileweatherbot.services.UpdateFieldExtractor.getMessageText;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Service
public class CityResolverUpdateProcessor extends AbstractUpdateProcessor {

    private final AlertInitStateProcessingService stateProcessingService;
    private final BotMessageProvider messageSource;
    private final CityResolverService cityRevolver;
    private final TgApiExecutor executor;

    public CityResolverUpdateProcessor(AlertInitStateProcessingService stateProcessingService,
                                       BotMessageProvider messageSource,
                                       CityResolverService cityRevolver,
                                       TgApiExecutor executor) {
        this.stateProcessingService = stateProcessingService;
        this.messageSource = messageSource;
        this.cityRevolver = cityRevolver;
        this.executor = executor;
    }

    @Override
    protected void process(Update update) {
        AlertInitDto dto = stateProcessingService.get(getChatId(update));
        CallbackQuery callback = update.getCallbackQuery();
        if (callback != null) {
            resolveCityFromCallback(dto, callback);
            return;
        }
        String city = getMessageText(update);
        List<CityDto> matchedCities = cityRevolver.getCities(city);
        if (matchedCities.size() == 1) {
            dto.setCity(matchedCities.get(0));
            stateProcessingService.storeForProcessing(dto);
        } else {
            dto.setMatchedCities(matchedCities);
            executor.execute(
                    new SendMessage()
                            .setChatId(getChatId(update))
                            .setText(messageSource.getMessage("city-guess", new Object[]{city}))
                            .setReplyMarkup(getReplyMarkup(matchedCities))
            );
            stateProcessingService.storeForProcessing(dto);
        }
    }

    private void resolveCityFromCallback(AlertInitDto dto, CallbackQuery callback) {
        String cityHash = callback.getData();
        CityDto city = dto.getMatchedCities().stream()
                .filter(cityDto -> cityDto.hashed().equals(cityHash))
                .findFirst().orElseThrow(() -> new IllegalStateException("City not found by provided hash!"));
        dto.setCity(city);
        dto.nextState();
        stateProcessingService.storeForProcessing(dto);
    }

    private ReplyKeyboard getReplyMarkup(List<CityDto> guessedCities) {
        return new InlineKeyboardMarkup()
                .setKeyboard(
                        guessedCities.stream().map(
                                (cityDto) -> Collections.singletonList(
                                        new InlineKeyboardButton()
                                                .setText(cityDto.getDisplayedName())
                                                .setCallbackData(cityDto.hashed())
                                )
                        ).collect(toList())
                );
    }

    @Override
    protected boolean applicableFor(Update update) {
        AlertInitDto dto = stateProcessingService.get(getChatId(update));
        return InitState.CITY == ofNullable(dto).map(AlertInitDto::getState).orElse(null);
    }
}
