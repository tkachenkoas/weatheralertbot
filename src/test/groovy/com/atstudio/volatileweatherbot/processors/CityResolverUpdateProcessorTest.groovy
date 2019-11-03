package com.atstudio.volatileweatherbot.processors

import com.atstudio.volatileweatherbot.bot.TgApiExecutor
import com.atstudio.volatileweatherbot.models.CityDto
import com.atstudio.volatileweatherbot.models.InitState
import com.atstudio.volatileweatherbot.models.SubscriptionDto
import com.atstudio.volatileweatherbot.services.api.BotMessageProvider
import com.atstudio.volatileweatherbot.services.api.CityResolverService
import com.atstudio.volatileweatherbot.services.api.SubscriptionCacheService
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static com.atstudio.volatileweatherbot.TestJsonHelper.getPlainMessageUpdate
import static com.atstudio.volatileweatherbot.services.UpdateFieldExtractor.getChatId
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class CityResolverUpdateProcessorTest extends GroovyTestCase {

    @Mock SubscriptionCacheService cacheService
    @Mock BotMessageProvider messageSource
    @Mock CityResolverService cityRevolver
    @Mock TgApiExecutor executor

    CityResolverUpdateProcessor underTest

    @BeforeMethod
    void init() {
        MockitoAnnotations.initMocks(this)
        underTest = new CityResolverUpdateProcessor(cacheService, messageSource, cityRevolver, executor)
    }

    @Test
    void isApplicableWhenCachedInitStateIsCity() {
        Update update = getPlainMessageUpdate()
        provideInitCityState(update)
        assert underTest.applicableFor(update)
    }

    private provideInitCityState(Update update) {
        when(cacheService.get(eq(getChatId(update)))).thenReturn(
                SubscriptionDto.builder()
                        .chatId(getChatId(update))
                        .state(InitState.CITY)
                        .build())
    }

    @Test
    void isNotApplicableWhenNoCachedStateOrStateIsNotCity() {
        Update update = getPlainMessageUpdate()
        assert !underTest.applicableFor(update)

        cacheService.save(
                SubscriptionDto.builder()
                        .chatId(getChatId(update))
                        .state(null)
                        .build())

        assert !underTest.applicableFor(update)
    }

    @Test
    void willAskForCityGuessMessageWithUsersInputCity() {
        String city = "USR_CITY"
        Update update = getPlainMessageUpdate(city)

        underTest.process(update)
        ArgumentCaptor<Object[]> messageArgsCaptor = ArgumentCaptor.forClass(Object[])
        verify(messageSource, times(1)).getMessage(eq('city-guess'), messageArgsCaptor.capture())

        assert messageArgsCaptor.getValue()[0] == city
    }

    @Test
    void willSendGuessedCitiesOnKeyboard() {
        String city = "USR_CITY"
        def keybMessage = 'Message with keyboard'
        when(messageSource.getMessage(eq('city-guess'), any())).thenReturn(keybMessage)
        when(cityRevolver.getCities(eq(city))).thenReturn(
                (1..3 as List).collect({
                    new CityDto("id${it}", "name${it}")
                })
        )
        Update update = getPlainMessageUpdate(city)
        provideInitCityState(update)

        assert underTest.willTakeCareOf(update)

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage)
        verify(executor, times(1)).execute(sendMessageCaptor.capture())

        SendMessage message = sendMessageCaptor.getValue()
        assert message.getChatId() == "${getChatId(update)}"
        assert message.getText() == keybMessage

        InlineKeyboardMarkup markup = message.getReplyMarkup() as InlineKeyboardMarkup
        assert markup.getKeyboard().size() == 3
        assert (0..2).each({
            def keyboard = markup.getKeyboard()[it]
            keyboard.size() == 1
            keyboard[0].text == "name${it+1}"
        })
    }

}
