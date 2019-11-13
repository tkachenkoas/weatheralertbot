package com.atstudio.volatileweatherbot

import com.google.gson.Gson
import com.google.maps.model.GeocodingResult
import groovy.json.JsonSlurper
import org.openweathermap.api.model.currentweather.CurrentWeather
import org.telegram.telegrambots.meta.api.objects.Update

class TestJsonHelper {

    /**
     *  gson is required when object fields have direct accessors, not via getters and setters
     */
    private static Gson gson = new Gson();
    private static JsonSlurper slurper = new JsonSlurper();

    static Update getPlainMessageUpdate(String message = 'default-message') {
        Update update = getUpdateFromFile('plain-message-update.json')
        update.message.text = message
        return update
    }

    static Update getUpdateFromFile(String fileName) {
        return slurper.parse(
                this.getResourceAsStream("/jsonmocks/telegram/${fileName}") as InputStream
        ) as Update
    }

    static GeocodingResult[] geocodingsFromFile(String file) {
        return gson.fromJson(
                this.getResourceAsStream("/jsonmocks/googlemaps/${file}").getText("UTF-8"),
                GeocodingResult[]
        )
    }

    static CurrentWeather getWeatherForecast(String fileName) {
        return slurper.parse(
                this.getResourceAsStream("/jsonmocks/openweather/${fileName}") as InputStream
        ) as CurrentWeather
    }


}
