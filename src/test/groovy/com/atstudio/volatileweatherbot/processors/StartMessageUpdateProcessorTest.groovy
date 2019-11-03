package com.atstudio.volatileweatherbot.processors

import com.atstudio.volatileweatherbot.bot.TgApiExecutor
import com.atstudio.volatileweatherbot.models.InitState
import com.atstudio.volatileweatherbot.models.SubscriptionDto
import com.atstudio.volatileweatherbot.services.api.BotMessageProvider
import com.atstudio.volatileweatherbot.services.api.SubscriptionCacheService
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import static com.atstudio.volatileweatherbot.TestJsonHelper.getPlainMessageUpdate
import static com.atstudio.volatileweatherbot.services.UpdateFieldExtractor.getChatId
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify

class StartMessageUpdateProcessorTest {

    @Mock TgApiExecutor executor
    @Mock BotMessageProvider messageSource
    @Mock SubscriptionCacheService cacheService

    StartMessageUpdateProcessor underTest

    @BeforeMethod
    void init() {
        MockitoAnnotations.initMocks(this)
        underTest = new StartMessageUpdateProcessor(executor, messageSource, cacheService)
    }

    @DataProvider(name="startSubscribe")
    static Object[][] messages() {
        return [["/subscribe"], ["/start"]] as Object[][]
    }


    @Test
    void processingWillSendCityMessage() {
        String text = "Specify-city-text"
        Mockito.when(messageSource.getMessage(eq("specify-city"))).thenReturn(text)
        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage)

        Update update = startUpdate();
        underTest.process(update)

        verify(executor, times(1)).execute(messageCaptor.capture())

        SendMessage message = messageCaptor.getValue()
        assert message.getChatId() == "${update.message.chatId}"
        assert message.getText() == text
    }

    @Test
    void willSetSubscriptionCacheToBaseState() {
        Update update = startUpdate();
        Long chatId = getChatId(update)

        underTest.process(update)

        ArgumentCaptor<SubscriptionDto> dtoArgumentCaptor = ArgumentCaptor.forClass(SubscriptionDto)
        verify(cacheService, times(1)).save(dtoArgumentCaptor.capture())

        SubscriptionDto dto = dtoArgumentCaptor.getValue()
        assert dto?.getChatId() == chatId
        assert dto?.getState() == InitState.CITY
    }


    @Test(dataProvider = "startSubscribe")
    void isApplicableToStartAndSubscribeUpdates(String message) {
        assert underTest.applicableFor(getPlainMessageUpdate(message))
    }

    Update startUpdate() {
        return getPlainMessageUpdate('/start')
    }

}