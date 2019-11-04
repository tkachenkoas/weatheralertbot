package com.atstudio.volatileweatherbot.services.util

import org.telegram.telegrambots.meta.api.objects.Update
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import static com.atstudio.volatileweatherbot.TestJsonHelper.getPlainMessageUpdate
import static com.atstudio.volatileweatherbot.TestJsonHelper.getUpdateFromFile

class UpdateFieldExtractorTest extends GroovyTestCase {

    class DataHolder {
        String text
        Long chatId
    }

    @DataProvider(name = "updates")
    static Object[][] messages() {
        return [
                [getPlainMessageUpdate("target-message"),
                 [text: 'target-message', chatId: 163655430L] as DataHolder],
                [getUpdateFromFile('with-callback-update.json'),
                 [text: 'Source-message-text', chatId: 163655430L] as DataHolder],
        ] as Object[][]
    }

    @Test(dataProvider = "updates")
    void extractMessage(Update update, DataHolder holder) {
        assert UpdateFieldExtractor.getMessageText(update) == holder.getText()
    }

    @Test(dataProvider = "updates")
    void extractChatId(Update update, DataHolder holder) {
        assert UpdateFieldExtractor.getChatId(update) == holder.getChatId()
    }

}
