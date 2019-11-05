package com.atstudio.volatileweatherbot.services.updateprocessors.initalert

import com.atstudio.volatileweatherbot.bot.TgApiExecutor
import com.atstudio.volatileweatherbot.models.AlertInitDto
import com.atstudio.volatileweatherbot.models.CityDto
import com.atstudio.volatileweatherbot.models.InitStage
import com.atstudio.volatileweatherbot.models.WeatherAlert
import com.atstudio.volatileweatherbot.repository.AlertRepository
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class SaveAlertStageProcessorTest {

    @Mock AlertRepository repository;
    @Mock TgApiExecutor executor;
    @Mock BotMessageProvider messageProvider;

    SaveAlertStageProcessor underTest;

    @BeforeMethod
    void init() {
        MockitoAnnotations.initMocks(this)
        underTest = new SaveAlertStageProcessor(repository, executor, messageProvider)
    }

    @Test
    void willSaveToRepoAndSendChatMessageWhenDone() {
        AlertInitDto dto = new AlertInitDto(1234L)
        dto.setStage(InitStage.READY_TO_SAVE)
        dto.setCity(
                [
                        cityCode: 'city',
                        lat: 10.0,
                        lng: 20.0
                ] as CityDto
        )

        def chatText = "Alert was created"
        when(messageProvider.getMessage(eq("alert-created"))).thenReturn(chatText)

        underTest.process(new Update(), dto)

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage)
        verify(executor, times(1)).execute(sendMessageCaptor.capture())
        SendMessage sendMessage = sendMessageCaptor.getValue()
        assert sendMessage.getChatId() == '' + dto.getChatId()
        assert sendMessage.getText() == chatText

        ArgumentCaptor<WeatherAlert> alertCaptor = ArgumentCaptor.forClass(WeatherAlert)
        verify(repository, times(1)).save(alertCaptor.capture())
        WeatherAlert stored = alertCaptor.getValue()
        assert stored.getChatId() == dto.getChatId()
        assert stored.getCityCode() == 'city'
        assert stored.getLat() == 10.0
        assert stored.getLng() == 20.0
    }


}