package com.atstudio.volatileweatherbot

import groovy.json.JsonSlurper
import org.telegram.telegrambots.meta.api.objects.Update

class TestJsonHelper {

    static Update getProcessorUpdate(String fileName) {
        return new JsonSlurper().parse(
                this.getResourceAsStream("/jsonmocks/processors/${fileName}") as InputStream
        ) as Update
    }

}
