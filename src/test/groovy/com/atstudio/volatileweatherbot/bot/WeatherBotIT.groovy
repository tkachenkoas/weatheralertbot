package com.atstudio.volatileweatherbot.bot

import com.atstudio.volatileweatherbot.services.external.weather.OpenWeatherMapApiAccessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.jdbc.JdbcTestUtils
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.time.LocalTime
import java.time.ZoneId

import static com.atstudio.volatileweatherbot.TestJsonHelper.getPlainMessageUpdate
import static com.atstudio.volatileweatherbot.TestJsonHelper.getUpdateWithCallBack
import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.LOCATIONS_TABLE_NAME
import static com.atstudio.volatileweatherbot.repository.weatheralert.WeatherAlertColumns.WEATHER_ALERTS_TABLE
import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.FORECAST_DETAILS_TABLE
import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.WEATHER_FORECAST_TABLE
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables

@ContextConfiguration(classes = BotTestConfigExcludingTgBeans)
@DirtiesContext
class WeatherBotIT extends AbstractTestNGSpringContextTests {
    @Autowired UpdateHandler updateHandler
    @Autowired TgApiExecutor executor
    @Autowired OpenWeatherMapApiAccessor weatherApiAccessor

    @Autowired List<BotApiMethod> executedMethods

    @Autowired JdbcTemplate template
    @Autowired NamedParameterJdbcTemplate named

    @BeforeMethod
    @AfterMethod
    void clean() {
        deleteFromTables(template, WEATHER_ALERTS_TABLE, LOCATIONS_TABLE_NAME, FORECAST_DETAILS_TABLE, WEATHER_FORECAST_TABLE)
    }

    @Test
    void willSendNotificationToRainAlert() {
        // init
        updateHandler.handle(getPlainMessageUpdate("/subscribe"))
        // City
        updateHandler.handle(getPlainMessageUpdate("Brisbane"))
        // Current brisbane time minus 1 minute
        LocalTime brisbaneTime = LocalTime.now(ZoneId.of("Australia/Brisbane")).plusMinutes(-1)
        def time = LocalTime.of(brisbaneTime.getHour(), brisbaneTime.getMinute())
        updateHandler.handle(getPlainMessageUpdate(time.toString()))
        sleep(3000)

        def sentMessages = executedMethods.collect({ (it as SendMessage).getText() + "\n" })
        def sentAlerts = sentMessages.findAll({ it.contains("Brisbane") && it.contains('22:00') }) // according to forecast, rain is at 22.00
        assert sentAlerts.size() == 1

        // might be an excessive check: will get weather only once
        verify(weatherApiAccessor, times(1)).getHourlyForecast(any())
    }

    @Test
    void alertWillBeRemovedWithSubscriptionListClick() {
        // init
        updateHandler.handle(getPlainMessageUpdate("/subscribe"))
        // City
        updateHandler.handle(getPlainMessageUpdate("Brisbane"))
        // Time -> alert is scheduled for next minute
        LocalTime brisbaneTime = LocalTime.now(ZoneId.of("Australia/Brisbane")).plusMinutes(1)
        def time = LocalTime.of(brisbaneTime.getHour(), brisbaneTime.getMinute())
        updateHandler.handle(getPlainMessageUpdate(time.toString()))

        updateHandler.handle(getPlainMessageUpdate("/alerts"))

        def callbackData = ((executedMethods.last() as SendMessage).getReplyMarkup() as InlineKeyboardMarkup).getKeyboard()[0][0].callbackData
        Update updateWithRemoveCallBack = getUpdateWithCallBack(callbackData)

        updateHandler.handle(updateWithRemoveCallBack)

        assert JdbcTestUtils.countRowsInTable(template, "t_weather_alerts") == 0
    }

}
