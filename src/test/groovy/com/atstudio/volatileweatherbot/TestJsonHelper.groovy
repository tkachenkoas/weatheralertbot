package com.atstudio.volatileweatherbot

import com.google.gson.Gson
import com.google.maps.model.GeocodingResult
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

    static GeocodingResult[] geocodingsFromFile(String file) {
        return new Gson().fromJson(
                this.getResourceAsStream("/jsonmocks/googleapi/${file}").getText("UTF-8"),
                GeocodingResult[]
        )
    }

}
