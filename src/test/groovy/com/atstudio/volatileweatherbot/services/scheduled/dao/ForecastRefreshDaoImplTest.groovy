package com.atstudio.volatileweatherbot.services.scheduled.dao

import com.atstudio.volatileweatherbot.models.domain.Location
import com.atstudio.volatileweatherbot.repository.RepoConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.time.ZoneId
import java.util.concurrent.ThreadLocalRandom

import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.LOCATIONS_TABLE_NAME
import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.FORECAST_DETAILS_TABLE
import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.WEATHER_FORECAST_TABLE
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables

@ContextConfiguration(classes = RepoConfig)
@Import([ForecastRefreshDaoImpl])
class ForecastRefreshDaoImplTest extends AbstractTestNGSpringContextTests {

    @Autowired ForecastRefreshDaoImpl underTest
    @Autowired JdbcTemplate template

    @BeforeMethod
    @AfterMethod
    void cleanDb() {
        deleteFromTables(template, FORECAST_DETAILS_TABLE, WEATHER_FORECAST_TABLE, LOCATIONS_TABLE_NAME)
    }

    @Test
    void willGetLocationsWhenAlertIsWithinOneHour() {

    }

    void createTestData() {
        template.update("INSERT INTO t_locations (code, lat, lng, timezone) values ('cityCode', 10, 15, 'Australia/Brisbane')")

    }


}