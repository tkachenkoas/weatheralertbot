package com.atstudio.volatileweatherbot.services.impl

import com.atstudio.volatileweatherbot.bot.TgApiExecutor
import com.atstudio.volatileweatherbot.models.AlertInitDto
import com.atstudio.volatileweatherbot.models.CityDto
import com.atstudio.volatileweatherbot.models.InitState
import com.atstudio.volatileweatherbot.models.WeatherAlert
import com.atstudio.volatileweatherbot.repository.AlertRepository
import com.atstudio.volatileweatherbot.services.api.BotMessageProvider
import com.google.common.cache.Cache
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class AlertInitStateProcessingServiceImplTest {

    @Mock Cache<Long, AlertInitDto> cache;
    @Mock AlertRepository repository;
    @Mock TgApiExecutor executor;
    @Mock BotMessageProvider messageProvider;

    AlertInitStateProcessingServiceImpl underTest;

    @BeforeMethod
    void init() {
        MockitoAnnotations.initMocks(this)
        underTest = new AlertInitStateProcessingServiceImpl(cache, repository, executor, messageProvider)
    }

    @Test
    void willInitDtoStateAndStoreToCache() {
        AlertInitDto dto = new AlertInitDto()
        dto.setChatId(1234L)

        underTest.storeForProcessing(dto)

        ArgumentCaptor<AlertInitDto> dtoArgumentCaptor = ArgumentCaptor.forClass(AlertInitDto)
        verify(cache, times(1)).put(eq(dto.getChatId()), dtoArgumentCaptor.capture())
        AlertInitDto cached = dtoArgumentCaptor.getValue()
        assert cached.getState() == InitState.CITY
    }

    @Test
    void willSaveToRepoWhenDone() {
        AlertInitDto dto = new AlertInitDto()
        dto.setChatId(1234L)
        dto.setState(InitState.DONE)
        dto.setCity(
                [
                        lat: 10.0,
                        lng: 20.0
                ] as CityDto
        )

        def chatText = "Alert was created"
        when(messageProvider.getMessage(eq("alert-created"))).thenReturn(chatText)

        underTest.storeForProcessing(dto)

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage)
        verify(executor, times(1)).execute(sendMessageCaptor.capture())
        SendMessage sendMessage = sendMessageCaptor.getValue()
        assert sendMessage.getChatId() == '' + dto.getChatId()
        assert sendMessage.getText() == chatText

        ArgumentCaptor<WeatherAlert> alertCaptor = ArgumentCaptor.forClass(WeatherAlert)
        verify(repository, times(1)).save(alertCaptor.capture())
        WeatherAlert stored = alertCaptor.getValue()
        assert stored.getChatId() == dto.getChatId()
        assert stored.getLat() == dto.getCity().getLat()
        assert stored.getLng() == dto.getCity().getLng()
    }

}
