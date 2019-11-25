package com.atstudio.volatileweatherbot.services.updateprocessors.initalert

import com.atstudio.volatileweatherbot.bot.TgApiExecutor
import com.atstudio.volatileweatherbot.models.domain.Location
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert
import com.atstudio.volatileweatherbot.models.dto.AlertInitDto
import com.atstudio.volatileweatherbot.models.dto.CityDto
import com.atstudio.volatileweatherbot.models.dto.InitStage
import com.atstudio.volatileweatherbot.repository.location.LocationRepository
import com.atstudio.volatileweatherbot.repository.weatheralert.AlertRepository
import com.atstudio.volatileweatherbot.services.external.geo.TimeZoneResolver
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.time.ZoneId

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class SaveAlertStageProcessorTest {

    @Mock AlertRepository alertRepository
    @Mock LocationRepository locationRepository
    @Mock TgApiExecutor executor
    @Mock BotMessageProvider messageProvider
    @Mock TimeZoneResolver timeZoneResolver

    SaveAlertStageProcessor underTest

    @BeforeMethod
    void init() {
        MockitoAnnotations.initMocks(this)
        when(alertRepository.save(any())).thenAnswer(new Answer<Object>() {
            @Override
            Object answer(InvocationOnMock invocation) throws Throwable {
                def alert = invocation.getArgument(0) as WeatherAlert
                alert.setUuid('uuid')
                return alert
            }
        })

        underTest = new SaveAlertStageProcessor(alertRepository, locationRepository, executor, messageProvider, timeZoneResolver)
    }

    @Test
    void willSaveToRepoAndSendChatMessageWhenDone() {
        AlertInitDto dto = new AlertInitDto(1234L)
        dto.setStage(InitStage.READY_TO_SAVE)
        dto.setCity(
                [
                        code        : 'city',
                        shortName   : 'city-displayed',
                        lat         : 10.0,
                        lng         : 20.0
                ] as CityDto
        )
        when(timeZoneResolver.timeZoneForCoordinates(eq(10.0 as BigDecimal), eq(20.0 as BigDecimal))).thenReturn(ZoneId.systemDefault())

        def chatText = "Alert was created"
        when(messageProvider.getMessage(eq("alert-created"))).thenReturn(chatText)

        underTest.process(new Update(), dto)

        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage)
        verify(executor, times(1)).execute(sendMessageCaptor.capture())
        SendMessage sendMessage = sendMessageCaptor.getValue()
        assert sendMessage.getChatId() == '' + dto.getChatId()
        assert sendMessage.getText() == chatText

        ArgumentCaptor<Location>locationCaptor = ArgumentCaptor.forClass(Location)
        verify(locationRepository, times(1)).createIfNotExists(locationCaptor.capture())
        Location location = locationCaptor.getValue()
        assert location.getCode() == dto.getCity().getCode()
        assert location.getLng() == dto.getCity().getLng()
        assert location.getLat() == dto.getCity().getLat()
        assert location.getTimeZone() == ZoneId.systemDefault()

        ArgumentCaptor<WeatherAlert> alertCaptor = ArgumentCaptor.forClass(WeatherAlert)
        verify(alertRepository, times(1)).save(alertCaptor.capture())
        WeatherAlert stored = alertCaptor.getValue()
        assert stored.getChatId() == dto.getChatId()
        assert stored.getLocationCode() == 'city'
        assert stored.getLocationLabel() == 'city-displayed'
    }


}
