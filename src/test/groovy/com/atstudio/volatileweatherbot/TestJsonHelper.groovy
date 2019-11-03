package com.atstudio.volatileweatherbot

import groovy.json.JsonSlurper
import org.telegram.telegrambots.meta.api.objects.Update

class TestJsonHelper {

    static Update getPlainMessageUpdate(String message = 'default-message') {
        Update update = getUpdateFromFile('plain-message-update.json')
        update.message.text = message
        return update
    }

    static Update getUpdateFromFile(String fileName) {
        return new JsonSlurper().parse(
                this.getResourceAsStream("/jsonmocks/processors/${fileName}") as InputStream
        ) as Update
    }

}
