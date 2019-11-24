package com.atstudio.volatileweatherbot.services.scheduled.dao

import com.atstudio.volatileweatherbot.models.domain.Location
import com.atstudio.volatileweatherbot.repository.RepoConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.sql.Timestamp
import java.time.*
import java.time.temporal.ChronoUnit

import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.LOCATIONS_TABLE_NAME
import static com.atstudio.volatileweatherbot.repository.weatheralert.WeatherAlertColumns.WEATHER_ALERTS_TABLE
import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.WEATHER_FORECAST_TABLE
import static java.time.ZoneId.systemDefault
import static java.util.concurrent.TimeUnit.HOURS
import static java.util.concurrent.TimeUnit.MINUTES
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables

@ContextConfiguration(classes = RepoConfig)
@Import([ForecastRefreshDaoImpl])
class ForecastRefreshDaoImplTest extends AbstractTestNGSpringContextTests {

    @Autowired
    ForecastRefreshDaoImpl underTest
    @Autowired
    NamedParameterJdbcTemplate namedParamsTemplate
    @Autowired
    JdbcTemplate jdbcTemplate

    @BeforeMethod
    @AfterMethod
    void cleanDb() {
        deleteFromTables(jdbcTemplate, WEATHER_ALERTS_TABLE, WEATHER_FORECAST_TABLE, LOCATIONS_TABLE_NAME)
    }

    static String BRISBANE_TIMEZONE = 'Australia/Brisbane'
    static String CITY_CODE = 'CITY_CODE'

    @Test
    void willGetLocationsWhenAlertIsWithinOneHour() {
        seedLocationAndAlert(brisbaneTimeWithDeviation(0, 59))
        def locations = underTest.getLocationsForForecastRefresh()
        assert locations.size() == 1
        def location = locations[0]
        assert location == new Location(CITY_CODE, 12 as BigDecimal, 15 as BigDecimal, ZoneId.of(BRISBANE_TIMEZONE))
    }

    @Test
    void wontGetLocationsWhenAlertIsLaterThanOneHour() {
        seedLocationAndAlert(brisbaneTimeWithDeviation(1, 01))
        def result = underTest.getLocationsForForecastRefresh()
        assert result.size() == 0
    }

    @Test
    void willGetLocationIfMoreThanOneHourSinceLastRefresh() {
        seedLocationAndAlert()
        seedForecast(Duration.of(61, ChronoUnit.MINUTES))
        def result = underTest.getLocationsForForecastRefresh()
        assert result.size() == 1
    }

    @Test
    void wontGetIfLessThanOneHourSinceLastRefresh() {
        seedLocationAndAlert()
        seedForecast(Duration.of(59, ChronoUnit.MINUTES))
        def result = underTest.getLocationsForForecastRefresh()
        assert result.size() == 0
    }

    void seedLocationAndAlert(LocalTime alertTime = brisbaneTimeWithDeviation(0, 59)) {
        jdbcTemplate.update("INSERT INTO t_locations (code, lat, lng, timezone) values ('${CITY_CODE}', 12, 15, '${BRISBANE_TIMEZONE}')")
        namedParamsTemplate.update("INSERT INTO t_weather_alerts (chat_id, location_code, location_label, alert_type, alert_time) VALUES " +
                "(123456, '${CITY_CODE}', 'Brisbane', 'RAIN', :alert_time)", [
                'alert_time': alertTime
        ] as Map)
    }

    void seedForecast(Duration sinceLastUpdate) {
        int minutes = sinceLastUpdate.toMinutes()
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), brisbaneTimeWithDeviation(0))
        namedParamsTemplate.update(
                "INSERT INTO t_weather_forecasts (uuid, location_code, period_start, period_end, update_time) VALUES " +
                        " ('some-uuid', '${CITY_CODE}', :start , :end, :update)",
                [
                        'start' : start,
                        'end'   : start.plusHours(8),
                        'update': new Timestamp(Instant.now().minus(minutes, ChronoUnit.MINUTES).toEpochMilli())
                ] as Map
        )
    }

    static LocalTime brisbaneTimeWithDeviation(int hrs = 1, int minutes = 0) {
        Instant now = Instant.now()
        def secondsOffset = { ZoneId zone -> zone.getOffset(now).getTotalSeconds() }
        long tzDeviation = secondsOffset(ZoneId.of(BRISBANE_TIMEZONE)) - secondsOffset(systemDefault())
        return LocalTime.now().plus(tzDeviation + HOURS.toSeconds(hrs) + MINUTES.toSeconds(minutes), ChronoUnit.SECONDS)
    }

}