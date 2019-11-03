package com.atstudio.volatileweatherbot.processors

import com.atstudio.volatileweatherbot.bot.TgApiExecutor
import com.atstudio.volatileweatherbot.services.api.BotMessageProvider
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static com.atstudio.volatileweatherbot.TestJsonHelper.getPlainMessageUpdate
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify

class HelpMessageUpdateProcessorTest {
    @Mock TgApiExecutor executor
    @Mock BotMessageProvider messageSource

    HelpMessageUpdateProcessor underTest

    @BeforeMethod
    void init() {
        MockitoAnnotations.initMocks(this)
        underTest = new HelpMessageUpdateProcessor(executor, messageSource)
    }

    @Test
    void willSendAboutBotMessage() {
        String text = "About message"
        Mockito.when(messageSource.getMessage(eq("about-bot"))).thenReturn(text)
        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage)

        underTest.process(helpUpdate())

        verify(executor, times(1)).execute(messageCaptor.capture())

        SendMessage message = messageCaptor.getValue()
        assert message.getChatId() == "${helpUpdate().message.chatId}"
        assert message.getText() == text
    }

    @Test
    void isApplicableForHelpMessage() {
        assert underTest.applicableFor(helpUpdate())
    }

    static Update helpUpdate() {
        getPlainMessageUpdate('/help')
    }

}
