package com.atstudio.volatileweatherbot.services.updateprocessors


import com.atstudio.volatileweatherbot.models.dto.AlertInitDto
import com.atstudio.volatileweatherbot.models.dto.InitStage
import com.atstudio.volatileweatherbot.models.dto.StagePhase
import com.atstudio.volatileweatherbot.services.updateprocessors.initalert.InitStageProcessor
import com.google.common.cache.Cache
import org.mockito.ArgumentCaptor
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.telegram.telegrambots.meta.api.objects.Update
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import static com.atstudio.volatileweatherbot.TestJsonHelper.getPlainMessageUpdate
import static com.atstudio.volatileweatherbot.TestJsonHelper.getUpdateFromFile
import static com.atstudio.volatileweatherbot.services.util.UpdateFieldExtractor.getChatId
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*
import static org.mockito.MockitoAnnotations.initMocks

class InitAlertUpdateProcessorTest extends GroovyTestCase {

    @Mock Cache<Long, AlertInitDto> cache
    @Mock InitStageProcessor cityProcessor
    @Mock InitStageProcessor storeProcessor

    InitAlertUpdateProcessor underTest

    @BeforeMethod
    void init() {
        initMocks(this)
        when(cityProcessor.applicableForStage()).thenReturn(InitStage.SPECIFY_CITY)
        when(storeProcessor.applicableForStage()).thenReturn(InitStage.READY_TO_SAVE)

        underTest = new InitAlertUpdateProcessor(cache, [cityProcessor, storeProcessor])
    }

    @DataProvider(name="startSubscribe")
    static Object[][] messages() {
        return [["/subscribe"], ["/start"]] as Object[][]
    }

    @Test(dataProvider = "startSubscribe")
    void isApplicableToStartAndSubscribeUpdates(String message) {
        Update update = getPlainMessageUpdate(message)

        when(cityProcessor.process(any(Update), any(AlertInitDto))).thenAnswer({ it.getArguments()[1] })
        Long chatId = getChatId(update)

        AlertInitDto expectedCached = new AlertInitDto(chatId)
        when(cache.getIfPresent(eq(chatId))).thenReturn(expectedCached)

        assert underTest.willTakeCareOf(update)

        ArgumentCaptor<AlertInitDto> captor = ArgumentCaptor.forClass(AlertInitDto)
        Mockito.verify(cache).put(Matchers.eq(chatId), captor.capture())

        AlertInitDto val = captor.getValue()
        assert val.getChatId() == expectedCached.getChatId()
        assert val.getStage() == expectedCached.getStage()
        assert val.getPhase() == expectedCached.getPhase()
    }

    @Test
    void willCallNextPhaseProcessorsWhenPhaseIsDone() {
        // given
        Update update = getUpdateFromFile('with-callback-update.json')
        Long chatId = getChatId(update)

        AlertInitDto cityStarted = new AlertInitDto(chatId)
        when(cache.getIfPresent(eq(chatId))).thenReturn(cityStarted)

        AlertInitDto cityDone = new AlertInitDto(chatId)
        cityDone.setPhase(StagePhase.DONE)

        when(cityProcessor.process(any(Update), any(AlertInitDto))).thenReturn(cityDone)
        when(storeProcessor.process(any(Update), any(AlertInitDto))).thenReturn(cityStarted)

        // when
        assert underTest.willTakeCareOf(update)

        // then
        verify(cityProcessor, times(1)).process(any(), any())
        verify(storeProcessor, times(1)).process(any(), any())
    }

    @Test
    void willTerminateWhenAlertIsCreated() {
        // given
        Update update = getUpdateFromFile('with-callback-update.json')
        Long chatId = getChatId(update)

        AlertInitDto readyToSave = new AlertInitDto(chatId)
        readyToSave.setStage(InitStage.READY_TO_SAVE)
        when(cache.getIfPresent(eq(chatId))).thenReturn(readyToSave)

        AlertInitDto doneSaving = new AlertInitDto(chatId)
        doneSaving.setStage(InitStage.READY_TO_SAVE)
        doneSaving.setPhase(StagePhase.DONE)
        when(storeProcessor.process(any(Update), any(AlertInitDto))).thenReturn(doneSaving)
        // when
        assert underTest.willTakeCareOf(update)

        // then
        verify(cityProcessor, never()).process(any(), any())
        verify(storeProcessor, times(1)).process(any(), any())

        verify(cache, times(1)).invalidate(eq(chatId))
    }

}
