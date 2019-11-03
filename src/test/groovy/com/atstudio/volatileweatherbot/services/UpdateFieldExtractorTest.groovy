package com.atstudio.volatileweatherbot.services

import com.atstudio.volatileweatherbot.TestJsonHelper
import org.telegram.telegrambots.meta.api.objects.Update
import org.testng.annotations.Test

class UpdateFieldExtractorTest extends GroovyTestCase {

    Update update = TestJsonHelper.getPlainMessageUpdate("target-message")

    @Test
    void extractFromUserId() {
        assert UpdateFieldExtractor.getUserId(update) == 163655430
    }

    @Test
    void extractMessage() {
        assert UpdateFieldExtractor.getMessageText(update) == 'target-message'
    }

    @Test
    void extractChatid() {
        assert UpdateFieldExtractor.getChatId(update) == 163655430
    }

}
