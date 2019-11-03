package com.atstudio.volatileweatherbot.processors

import com.atstudio.volatileweatherbot.bot.TgApiExecutor
import com.atstudio.volatileweatherbot.models.AlertInitDto
import com.atstudio.volatileweatherbot.models.CityDto
import com.atstudio.volatileweatherbot.models.InitState
import com.atstudio.volatileweatherbot.services.api.AlertInitStateProcessingService
import com.atstudio.volatileweatherbot.services.api.BotMessageProvider
import com.atstudio.volatileweatherbot.services.api.CityResolverService
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static com.atstudio.volatileweatherbot.TestJsonHelper.getPlainMessageUpdate
import static com.atstudio.volatileweatherbot.TestJsonHelper.getUpdateFromFile
import static com.atstudio.volatileweatherbot.services.UpdateFieldExtractor.getChatId
import static com.atstudio.volatileweatherbot.services.impl.CityResolverServiceImpl.rndCity
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class CityResolverUpdateProcessorTest extends GroovyTestCase {

    @Mock
    AlertInitStateProcessingService stateProcessingService
    @Mock
    BotMessageProvider messageSource
    @Mock
    CityResolverService cityRevolver
    @Mock
    TgApiExecutor executor

    CityResolverUpdateProcessor underTest

    @BeforeMethod
    void init() {
        MockitoAnnotations.initMocks(this)
        underTest = new CityResolverUpdateProcessor(stateProcessingService, messageSource, cityRevolver, executor)
    }

    @Test
    void isApplicableWhenCachedInitStateIsCity() {
        Update update = getPlainMessageUpdate()
        provideInitCityState(getChatId(update))
        assert underTest.applicableFor(update)
    }

    @Test
    void isApplicableForCallbackUpdate() {
        Update update = getUpdateFromFile('with-callback-update.json')
        provideInitCityState(getChatId(update))
        assert underTest.applicableFor(update)
    }

    private provideInitCityState(Long chatId) {
        when(stateProcessingService.get(eq(chatId))).thenReturn(
                AlertInitDto.builder()
                        .chatId(chatId)
                        .state(InitState.CITY)
                        .build())
    }

    @Test
    void isNotApplicableWhenNoCachedStateOrStateIsNotCity() {
        Update update = getPlainMessageUpdate()
        assert !underTest.applicableFor(update)

        when(stateProcessingService.get(eq(getChatId(update)))).thenReturn(
                AlertInitDto.builder()
                        .chatId(getChatId(update))
                        .state(InitState.DONE)
                        .build())

        assert !underTest.applicableFor(update)
    }

    @Test
    void willAskForCityGuessMessageWithUsersInputCity() {
        String city = "USR_CITY"
        Update update = getPlainMessageUpdate(city)

        provideInitCityState(getChatId(update))
        initCityGuesses(city)

        underTest.process(update)
        ArgumentCaptor<Object[]> messageArgsCaptor = ArgumentCaptor.forClass(Object[])
        verify(messageSource, times(1)).getMessage(eq('city-guess'), messageArgsCaptor.capture())

        assert messageArgsCaptor.getValue()[0] == city
    }

    @Test
    void whenResolvingCityWillSendGuessedCitiesOnKeyboard() {
        String city = "USR_CITY"
        def keybMessage = 'Message with keyboard'
        when(messageSource.getMessage(eq('city-guess'), any())).thenReturn(keybMessage)
        List<CityDto> cityGuesses = initCityGuesses(city)
        Update update = getPlainMessageUpdate(city)
        provideInitCityState(getChatId(update))

        assert underTest.willTakeCareOf(update)

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage)
        verify(executor, times(1)).execute(sendMessageCaptor.capture())

        ArgumentCaptor<AlertInitDto> dtoArgumentCaptor = ArgumentCaptor.forClass(AlertInitDto)
        verify(stateProcessingService, times(1)).storeForProcessing(dtoArgumentCaptor.capture())

        AlertInitDto dto = dtoArgumentCaptor.getValue()
        assert dto.getMatchedCities() as Set == cityGuesses as Set


        SendMessage message = sendMessageCaptor.getValue()
        assert message.getChatId() == "${getChatId(update)}"
        assert message.getText() == keybMessage

        InlineKeyboardMarkup markup = message.getReplyMarkup() as InlineKeyboardMarkup
        assert markup.getKeyboard().size() == 3
        assert (0..2).each({
            def keyboard = markup.getKeyboard()[it]
            keyboard.size() == 1
            keyboard[0].text == "Displayed ${it + 1}"
        })
    }

    List<CityDto> initCityGuesses(String city) {
        List<CityDto> cityGuesses = (1..3 as List).collect({
            randomCity("$it")
        })
        when(cityRevolver.getCities(eq(city))).thenReturn(cityGuesses)
        cityGuesses
    }

    @Test
    void whenKeyboardCallbackReceivedWillResolveCity() {
        Update update = getUpdateFromFile('with-callback-update.json')
        def cityGuesses = (1..3 as List).collect({
            randomCity("$it")
        })
        update.callbackQuery.data = cityGuesses[1].hashed()

        AlertInitDto startDto = AlertInitDto.builder()
                .chatId(getChatId(update))
                .state(InitState.CITY)
                .matchedCities(cityGuesses)
                .build()

        when(stateProcessingService.get(eq(getChatId(update)))).thenReturn(startDto)

        underTest.willTakeCareOf(update)

        ArgumentCaptor<AlertInitDto> dtoArgumentCaptor = ArgumentCaptor.forClass(AlertInitDto)
        verify(stateProcessingService, times(1)).storeForProcessing(dtoArgumentCaptor.capture())

        AlertInitDto targetDto = dtoArgumentCaptor.getValue()
        assert targetDto.getChatId() == startDto.getChatId()
        assert targetDto.getState() == InitState.DONE
        assert targetDto.getCity() == cityGuesses[1]
    }

    private static CityDto randomCity(String suffix) {
        rndCity("$suffix")
    }


}
