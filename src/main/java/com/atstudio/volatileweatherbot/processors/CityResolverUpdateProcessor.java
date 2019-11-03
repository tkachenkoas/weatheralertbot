package com.atstudio.volatileweatherbot.processors;

import com.atstudio.volatileweatherbot.bot.TgApiExecutor;
import com.atstudio.volatileweatherbot.models.CityDto;
import com.atstudio.volatileweatherbot.models.InitState;
import com.atstudio.volatileweatherbot.models.SubscriptionDto;
import com.atstudio.volatileweatherbot.services.api.BotMessageProvider;
import com.atstudio.volatileweatherbot.services.api.CityResolverService;
import com.atstudio.volatileweatherbot.services.api.SubscriptionCacheService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;

import static com.atstudio.volatileweatherbot.services.UpdateFieldExtractor.*;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Service
public class CityResolverUpdateProcessor extends AbstractUpdateProcessor {

    private final SubscriptionCacheService cacheService;
    private final BotMessageProvider messageSource;
    private final CityResolverService cityRevolver;
    private final TgApiExecutor executor;

    public CityResolverUpdateProcessor(SubscriptionCacheService cacheService,
                                       BotMessageProvider messageSource,
                                       CityResolverService cityRevolver,
                                       TgApiExecutor executor) {
        this.cacheService = cacheService;
        this.messageSource = messageSource;
        this.cityRevolver = cityRevolver;
        this.executor = executor;
    }

    @Override
    protected void process(Update update) {
        String city = getMessageText(update);
        List<CityDto> guessedCities = cityRevolver.getCities(city);

        executor.execute(
                new SendMessage()
                        .setChatId(getChatId(update))
                        .setText(messageSource.getMessage("city-guess", new Object[]{city}))
                        .setReplyMarkup(getReplyMarkup(guessedCities))
        );
    }

    private ReplyKeyboard getReplyMarkup(List<CityDto> guessedCities) {
        return new InlineKeyboardMarkup()
                .setKeyboard(
                        guessedCities.stream().map(
                                (cityDto) -> Collections.singletonList(
                                        new InlineKeyboardButton()
                                                .setText(cityDto.getDisplayedName())
                                                .setCallbackData(cityDto.getCityId())
                                )
                        ).collect(toList())
                );
    }

    @Override
    protected boolean applicableFor(Update update) {
        SubscriptionDto dto = cacheService.get(getUserId(update));
        return InitState.CITY == ofNullable(dto).map(SubscriptionDto::getState).orElse(null);
    }
}
