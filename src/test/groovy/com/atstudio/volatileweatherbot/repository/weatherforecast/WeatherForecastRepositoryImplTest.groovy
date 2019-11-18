package com.atstudio.volatileweatherbot.repository.weatherforecast

import com.atstudio.volatileweatherbot.models.domain.AlertWeatherType
import com.atstudio.volatileweatherbot.models.domain.Location
import com.atstudio.volatileweatherbot.models.domain.forecast.ForecastDetails
import com.atstudio.volatileweatherbot.models.domain.forecast.RainInfo
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast
import com.atstudio.volatileweatherbot.repository.RepoConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.jdbc.JdbcTestUtils
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.util.concurrent.ThreadLocalRandom

import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.FORECAST_DETAILS_TABLE
import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.WEATHER_FORECAST_TABLE
import static java.time.LocalDateTime.of
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTableWhere

@ContextConfiguration(classes = RepoConfig)
@Import(WeatherForecastRepositoryImpl)
class WeatherForecastRepositoryImplTest extends AbstractTestNGSpringContextTests {

    @Autowired
    WeatherForecastRepositoryImpl underTest

    @Autowired
    JdbcTemplate template

    @BeforeMethod
    @AfterMethod
    void cleanDb() {
        JdbcTestUtils.deleteFromTables(template, FORECAST_DETAILS_TABLE)
        JdbcTestUtils.deleteFromTables(template, WEATHER_FORECAST_TABLE)
    }

    @Test
    void willStoreForecast() {
        WeatherForecast forecast = underTest.storeForecast(testForecast())

        assert 1 == countRowsInTableWhere(template, WEATHER_FORECAST_TABLE, "uuid = '${forecast.getUuid()}'")
        assert 2 == countRowsInTableWhere(template, FORECAST_DETAILS_TABLE, "forecast_uuid = '${forecast.getUuid()}'")
    }

    @Test
    void willGetStoredForecastForLocationAndDateTime() {
        WeatherForecast source = testForecast()
        underTest.storeForecast(source)

        WeatherForecast stored = underTest.getLocationForecastForLocalTime(
                [code: source.getLocationCode()] as Location,
                of(2019, Month.NOVEMBER, 20, 15, 00)
        )

        assert stored == source
    }

    private static WeatherForecast testForecast() {
        WeatherForecast forecast = new WeatherForecast()
        forecast.setLocationCode('code')
        forecast.setPeriodStart(of(2019, Month.NOVEMBER, 20, 8, 00))
        forecast.setPeriodEnd(of(2019, Month.NOVEMBER, 20, 20, 00))
        forecast.setUpdateTime(Instant.now())
        forecast.setDetails([
                testDetails(forecast.getPeriodStart()),
                testDetails(forecast.getPeriodEnd())
        ] as List)
        return forecast
    }

    private static ForecastDetails testDetails(LocalDateTime dateTime) {
        Random rnd = ThreadLocalRandom.current();
        return [
                targetDateTime      : dateTime,
                expectedWeatherType : AlertWeatherType.RAIN,
                temperature         : rnd.nextInt(20, 30) as BigDecimal,
                temperatureDeviation: rnd.nextInt(2, 4) as BigDecimal,
                rainInfo            : new RainInfo(
                        rnd.nextInt(2, 4) as BigDecimal, randomAlphabetic(10)
                )
        ] as ForecastDetails;
    }

}
