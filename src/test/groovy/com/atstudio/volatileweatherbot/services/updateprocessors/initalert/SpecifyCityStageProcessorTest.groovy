package com.atstudio.volatileweatherbot.services.updateprocessors.initalert

import com.atstudio.volatileweatherbot.bot.TgApiExecutor
import com.atstudio.volatileweatherbot.models.dto.AlertInitDto
import com.atstudio.volatileweatherbot.models.dto.CityDto
import com.atstudio.volatileweatherbot.models.dto.StagePhase
import com.atstudio.volatileweatherbot.services.external.CityResolverService
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider
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
import static com.atstudio.volatileweatherbot.services.external.CityResolverServiceImpl.rndCity
import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getChatId
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class SpecifyCityStageProcessorTest {

    @Mock
    BotMessageProvider messageSource;
    @Mock
    CityResolverService cityRevolver;
    @Mock
    TgApiExecutor executor;

    SpecifyCityStageProcessor underTest

    @BeforeMethod
    void init() {
        MockitoAnnotations.initMocks(this)
        underTest = new SpecifyCityStageProcessor(messageSource, cityRevolver, executor)
    }

    @Test
    void onStartPhaseWillSendMessage() {
        String text = "Specify-city-text"
        when(messageSource.getMessage(eq("specify-city"))).thenReturn(text)
        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage)
        Update update = getPlainMessageUpdate()
        underTest.process(update, new AlertInitDto(getChatId(update)))

        verify(executor, times(1)).execute(messageCaptor.capture())

        SendMessage message = messageCaptor.getValue()
        assert message.getChatId() == "${update.message.chatId}"
        assert message.getText() == text
    }

    @Test
    void whenManyMatchingCitiesWillSendMessageToChatAndCacheCities() {
        String city = "USR_CITY"
        Update update = getPlainMessageUpdate(city)

        // init city provider mock
        List<CityDto> cityGuesses = initCityGuesses(city)

        def keybMessage = 'Message with keyboard'
        when(messageSource.getMessage(eq('city-guess'), any())).thenReturn(keybMessage)

        AlertInitDto processing = provideProcessedInitDto(getChatId(update))

        AlertInitDto result = underTest.process(update, processing)
        assert result.getPhase() == StagePhase.PROCESSING
        assert result.getMatchedCities() as Set == cityGuesses as Set

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
            keyboard[0].text == "Displayed ${it + 1}"
        })
    }

    @Test
    void willResolveCityWhenSingleApiResult() {
        String city = "USR_CITY"
        Update update = getPlainMessageUpdate(city)

        // init city provider mock
        List<CityDto> cityGuesses = initCityGuesses(city, 1)

        AlertInitDto startDto = provideProcessedInitDto(getChatId(update))

        AlertInitDto targetDto = underTest.process(update, startDto)

        assert targetDto.getChatId() == startDto.getChatId()
        assert targetDto.getPhase() == StagePhase.DONE
        assert targetDto.getCity() == cityGuesses[0]
    }

    @Test
    void whenKeyboardCallbackReceivedWillResolveCity() {
        Update update = getUpdateFromFile('with-callback-update.json')
        def cityGuesses = (1..3 as List).collect({
            randomCity("$it")
        })
        update.callbackQuery.data = cityGuesses[1].getCode()

        AlertInitDto startDto = provideProcessedInitDto(getChatId(update))
        startDto.setMatchedCities(cityGuesses)

        AlertInitDto targetDto = underTest.process(update, startDto)

        assert targetDto.getChatId() == startDto.getChatId()
        assert targetDto.getPhase() == StagePhase.DONE
        assert targetDto.getCity() == cityGuesses[1]
    }

    static AlertInitDto provideProcessedInitDto(long chatId) {
        AlertInitDto processingCity = new AlertInitDto(chatId)
        processingCity.setPhase(StagePhase.PROCESSING);
        return processingCity;
    }

    List<CityDto> initCityGuesses(String city, int count = 3) {
        List<CityDto> cityGuesses = (1..count as List).collect({
            randomCity("$it")
        })
        when(cityRevolver.getCities(eq(city))).thenReturn(cityGuesses)
        cityGuesses
    }

    private static CityDto randomCity(String suffix) {
        rndCity("$suffix")
    }

}
