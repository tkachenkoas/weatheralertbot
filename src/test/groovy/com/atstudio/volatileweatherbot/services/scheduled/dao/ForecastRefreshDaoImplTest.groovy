package com.atstudio.volatileweatherbot.services.scheduled.dao

import com.atstudio.volatileweatherbot.repository.RepoConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.jdbc.JdbcTestUtils
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod

import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.FORECAST_DETAILS_TABLE
import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.WEATHER_FORECAST_TABLE

@ContextConfiguration(classes = RepoConfig)
@Import([ForecastRefreshDaoImpl])
class ForecastRefreshDaoImplTest extends AbstractTestNGSpringContextTests {

    @Autowired ForecastRefreshDaoImpl underTest
    @Autowired JdbcTemplate template

    @BeforeMethod
    @AfterMethod
    void cleanDb() {
        JdbcTestUtils.deleteFromTables(template, FORECAST_DETAILS_TABLE, WEATHER_FORECAST_TABLE)
    }



}