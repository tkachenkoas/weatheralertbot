package com.atstudio.volatileweatherbot.bot


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.jdbc.JdbcTestUtils
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static com.atstudio.volatileweatherbot.TestJsonHelper.getPlainMessageUpdate
import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.LOCATIONS_TABLE_NAME
import static com.atstudio.volatileweatherbot.repository.weatheralert.WeatherAlertColumns.WEATHER_ALERTS_TABLE
import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.WEATHER_FORECAST_TABLE
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTableWhere
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables

@ContextConfiguration(classes = BotTestConfigExcludingTgBeans)
@DirtiesContext
class UpdateHandlerIT extends AbstractTestNGSpringContextTests {

    @Autowired
    UpdateHandler updateHandler
    @Autowired
    JdbcTemplate template
    @Autowired
    TgApiExecutor executor

    @Autowired
    List<BotApiMethod> executedMethods

    @BeforeMethod
    @AfterMethod
    void clean() {
        deleteFromTables(template, WEATHER_ALERTS_TABLE, LOCATIONS_TABLE_NAME, WEATHER_FORECAST_TABLE)
    }

    @Test
    void fullAlertCreationSceneryCanBeExecuted() {
        // Initiating weather alert subscription
        updateHandler.handle(getPlainMessageUpdate("/subscribe"))

        // we're asked for a city; since api response is stubbed,
        // one variant will be returned, and alert will be created
        updateHandler.handle(getPlainMessageUpdate("Brisbane"))

        assert JdbcTestUtils.countRowsInTable(template, "t_weather_alerts") == 1
    }

    @Test
    void defaultCreatedAlertTypeIsRain() {
        // init
        updateHandler.handle(getPlainMessageUpdate("/subscribe"))
        // City
        updateHandler.handle(getPlainMessageUpdate("Brisbane"))
        assert countRowsInTableWhere(template, "t_weather_alerts", "alert_type='RAIN'") == 1
    }

    // for debugging purposes
    void logLastExecutedUpdate() {
        println("Last executed method: ${executedMethods.last()}")
    }

}
