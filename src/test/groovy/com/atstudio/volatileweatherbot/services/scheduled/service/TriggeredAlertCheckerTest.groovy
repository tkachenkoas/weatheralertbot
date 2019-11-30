package com.atstudio.volatileweatherbot.services.scheduled.service

import com.atstudio.volatileweatherbot.bot.TgApiExecutor
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast
import com.atstudio.volatileweatherbot.repository.weatheralert.AlertRepository
import com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastRepository
import com.atstudio.volatileweatherbot.services.scheduled.service.weatheralert.ChatAlertContext
import com.atstudio.volatileweatherbot.services.scheduled.service.weatheralert.ForecastToAlertMatchProcessor
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*
import static org.mockito.MockitoAnnotations.initMocks

class TriggeredAlertCheckerTest {

    @Mock
    AlertRepository alertRepository
    @Mock
    WeatherForecastRepository forecastRepository
    List<ForecastToAlertMatchProcessor> matchProcessors
    @Mock
    TgApiExecutor executor
    @Mock
    BotMessageProvider messageProvider

    TriggeredAlertChecker underTest

    @BeforeMethod
    void init() {
        initMocks(this)
        matchProcessors = new ArrayList<>()
        underTest = new TriggeredAlertChecker(alertRepository, forecastRepository, matchProcessors, executor, messageProvider)
    }

    @Test
    void complexScenery() {
        //given
        WeatherAlert alert1 = [locationCode: 'loc1', locationLabel: 'label'] as WeatherAlert
        WeatherAlert alert2 = [locationCode: 'loc2'] as WeatherAlert

        when(alertRepository.getTriggeredAlerts())
                .thenReturn([alert1, alert2] as List)

        WeatherForecast forecast1 = mock(WeatherForecast)
        WeatherForecast forecast2 = mock(WeatherForecast)

        when(forecastRepository.getLatestForecastForLocation(eq('loc1'))).thenReturn(forecast1)
        when(forecastRepository.getLatestForecastForLocation(eq('loc2'))).thenReturn(forecast2)

        ForecastToAlertMatchProcessor triggeredProcessor = mock(ForecastToAlertMatchProcessor)
        when(triggeredProcessor.checkCurrentForecastForAlertMatch(eq(alert1), eq(forecast1))).thenAnswer(new Answer<Object>() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                return ChatAlertContext.addChatMessageForLocation(1L, 'label', "Alert message")
            }
        })

        matchProcessors.addAll([triggeredProcessor, mock(ForecastToAlertMatchProcessor)])

        when(messageProvider.getMessage(eq('alert-triggered'), eq('label'))).thenReturn('Alert header')

        //  when
        underTest.checkTriggeredAlerts()

        //  then
        matchProcessors.each {
            verify(it, times(2)).checkCurrentForecastForAlertMatch(any(WeatherAlert), any(WeatherForecast))
        }

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage)
        verify(executor, times(1)).execute(messageCaptor.capture())

        def mess = messageCaptor.getValue()
        assert mess.getChatId() == '1'
        assert mess.getText().contains('Alert message')
        assert mess.getText().contains('Alert header')
    }

}
