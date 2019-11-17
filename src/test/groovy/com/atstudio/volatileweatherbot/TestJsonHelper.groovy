package com.atstudio.volatileweatherbot

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.maps.model.GeocodingResult
import groovy.json.JsonSlurper
import org.openweathermap.api.gson.WindDirectionDeserializer
import org.openweathermap.api.gson.WindDirectionSerializer
import org.openweathermap.api.model.WindDirection
import org.openweathermap.api.model.forecast.ForecastInformation
import org.openweathermap.api.model.forecast.hourly.HourlyForecast
import org.telegram.telegrambots.meta.api.objects.Update

import static org.apache.commons.io.IOUtils.toString

class TestJsonHelper {

    /**
     *  gson is preferred when object fields have direct accessors, not via getters and setters
     */
    private static Gson gson = new Gson();
    private static Gson openWeatherGson =  new GsonBuilder()
            .registerTypeAdapter(WindDirection.class, new WindDirectionDeserializer())
            .registerTypeAdapter(WindDirection.class, new WindDirectionSerializer())
            .create();

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
                toString(this.getResourceAsStream("/jsonmocks/googlemaps/${file}")),
                GeocodingResult[]
        )
    }

    static ForecastInformation<HourlyForecast> getWeatherForecast(String fileName) {

        return openWeatherGson.fromJson(
                toString(this.getResourceAsStream("/jsonmocks/openweather/${fileName}")),
                HourlyForecast.TYPE
        )
    }


}
