package com.atstudio.volatileweatherbot.services.updateprocessors.initalert

import com.atstudio.volatileweatherbot.bot.TgApiExecutor
import com.atstudio.volatileweatherbot.models.dto.AlertInitDto
import com.atstudio.volatileweatherbot.models.dto.StagePhase
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.time.LocalTime

import static com.atstudio.volatileweatherbot.TestJsonHelper.getPlainMessageUpdate
import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getChatId
import static java.lang.Integer.parseInt
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*
import static org.mockito.MockitoAnnotations.initMocks

class SpecifyAlertTimeStageProcessorTest {

    @Mock BotMessageProvider messageSource
    @Mock TgApiExecutor executor

    SpecifyAlertTimeStageProcessor underTest

    @BeforeMethod
    void init() {
        initMocks(this)
        underTest = new SpecifyAlertTimeStageProcessor(messageSource, executor)
    }

    @Test
    void onStartPhaseWillSendMessage() {
        String text = "Specify-time-text"
        when(messageSource.getMessage(eq("specify-time"))).thenReturn(text)
        Update update = getPlainMessageUpdate()
        underTest.process(update, new AlertInitDto(getChatId(update)))

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage)
        verify(executor, times(1)).execute(messageCaptor.capture())

        SendMessage message = messageCaptor.getValue()
        assert message.getChatId() == "${update.message.chatId}"
        assert message.getText() == text
    }

    @DataProvider(name="invalidTimeFormat")
    static Object[][] invalidTime() {
        return [["some text"], ["25:12"], ["12:65"]] as Object[][]
    }

    @Test(dataProvider = 'invalidTimeFormat')
    void willCheckBadFormat(String invalidTime) {
        when(messageSource.getMessageWithArgs(eq("bad-time-format"), eq(invalidTime))).thenReturn("Invalid time message")
        Update update = getPlainMessageUpdate(invalidTime)

        def initDto = new AlertInitDto(getChatId(update))
        initDto.setPhase(StagePhase.PROCESSING)

        def processed = underTest.process(update, initDto)
        assert processed.getPhase() == StagePhase.PROCESSING

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage)
        verify(executor, times(1)).execute(messageCaptor.capture())

        SendMessage message = messageCaptor.getValue()
        assert message.getChatId() == "${update.message.chatId}"
        assert message.getText() == "Invalid time message"
    }

    @DataProvider(name="validTimeFormat")
    static Object[][] validTime() {
        return [["14:00"], ["02:12"], ["8:00"]] as Object[][]
    }

    @Test(dataProvider = 'validTimeFormat')
    void willResolveTimeForCorrectTimeString(String time) {
        Update update = getPlainMessageUpdate(time)

        def initDto = new AlertInitDto(getChatId(update))
        initDto.setPhase(StagePhase.PROCESSING)

        def processed = underTest.process(update, initDto)
        assert processed.getPhase() == StagePhase.DONE

        String[] parts = time.split(":")
        assert processed.getAlertLocalTime() == LocalTime.of(parseInt(parts[0]),parseInt(parts[1]))

        verify(executor, never()).execute(any())
    }

}
