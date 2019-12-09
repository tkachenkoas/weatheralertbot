package com.atstudio.volatileweatherbot.repository.weatherforecast

import com.atstudio.volatileweatherbot.models.domain.WeatherType
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
import java.time.temporal.ChronoUnit
import java.util.concurrent.ThreadLocalRandom

import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.FORECAST_DETAILS_TABLE
import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.WEATHER_FORECAST_TABLE
import static java.time.Instant.now
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

        WeatherForecast stored = underTest.getLatestLocationForecastForLocalTime(
                source.getLocationCode(),
                of(2019, Month.NOVEMBER, 20, 15, 00)
        )

        assert stored == source
    }

    @Test
    void ofTwoForecastsForLocalTimeWillChooseLatest() {
        WeatherForecast latest = testForecast()
        underTest.storeForecast(latest)

        WeatherForecast earlier = testForecast(now().minus(1, ChronoUnit.HOURS))
        earlier.getDetails().add(testDetails(earlier.getPeriodStart().plusHours(4)))
        underTest.storeForecast(earlier)

        WeatherForecast stored = underTest.getLatestLocationForecastForLocalTime(
                latest.getLocationCode(),
                of(2019, Month.NOVEMBER, 20, 15, 00)
        )
        assert stored == latest
    }

    @Test
    void willGetLatestForecast() {
        WeatherForecast latest = testForecast()
        underTest.storeForecast(latest)

        WeatherForecast earlier = testForecast(now().minus(1, ChronoUnit.HOURS))
        earlier.getDetails().add(testDetails(earlier.getPeriodStart().plusHours(4)))
        underTest.storeForecast(earlier)

        WeatherForecast stored = underTest.getLatestForecastForLocation(latest.getLocationCode())
        assert stored == latest
    }

    private static WeatherForecast testForecast(Instant updateTime = now()) {
        WeatherForecast forecast = new WeatherForecast()
        forecast.setLocationCode('code')
        forecast.setPeriodStart(of(2019, Month.NOVEMBER, 20, 8, 00))
        forecast.setPeriodEnd(of(2019, Month.NOVEMBER, 20, 20, 00))
        forecast.setUpdateTime(updateTime)
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
                expectedWeatherType : WeatherType.RAIN,
                temperature         : rnd.nextInt(20, 30) as BigDecimal,
                temperatureDeviation: rnd.nextInt(2, 4) as BigDecimal,
                rainInfo            : new RainInfo(
                        rnd.nextInt(2, 4) as BigDecimal, randomAlphabetic(10)
                )
        ] as ForecastDetails;
    }

}
