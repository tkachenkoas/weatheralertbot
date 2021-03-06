package com.atstudio.volatileweatherbot.repository.weatheralert

import com.atstudio.volatileweatherbot.models.domain.WeatherType
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert
import com.atstudio.volatileweatherbot.repository.RepoConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.time.LocalTime

import static com.atstudio.volatileweatherbot.repository.TestDaoTimezoneHelper.*
import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.LOCATIONS_TABLE_NAME
import static com.atstudio.volatileweatherbot.repository.weatheralert.WeatherAlertColumns.WEATHER_ALERTS_TABLE
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables

@ContextConfiguration(classes = RepoConfig)
@Import(AlertRepositoryImpl)
class AlertRepositoryImplTest extends AbstractTestNGSpringContextTests {

    @Autowired JdbcTemplate template
    @Autowired AlertRepositoryImpl underTest

    @BeforeMethod
    @AfterMethod
    void cleanDb() {
        deleteFromTables(template, WEATHER_ALERTS_TABLE, LOCATIONS_TABLE_NAME)
    }

    @Test
    void willSaveAndRetrieveForChat() {
        template.update("INSERT INTO t_locations (code, lat, lng, timezone) values ('${CITY_CODE}', 10, 15, 'Europe/Moscow')")

        WeatherAlert alert = someAlert()

        underTest.save(alert)

        List<WeatherAlert> stored = underTest.getAlertsForChatId(alert.getChatId())

        assert stored.size() == 1
        assert stored[0] == alert
    }

    @Test
    void willRemoveAlert() {
        template.update("INSERT INTO t_locations (code, lat, lng, timezone) values ('${CITY_CODE}', 10, 15, 'Europe/Moscow')")

        WeatherAlert alert = someAlert()
        underTest.save(alert)

        assert underTest.removeByUuid(alert.getUuid())
        assert underTest.getAlertsForChatId(alert.getChatId()).size() == 0
    }

    @Test
    void willGetAlreadyTriggeredAlert() {
        createTestLocation()

        // Alert should have triggered one minute ago
        def someAlert = someAlert(brisbaneTimeWithDeviation(0, -1))
        underTest.save(someAlert)

        def triggered = underTest.getTriggeredAlerts()
        assert triggered.size() == 1
        assert triggered[0] == someAlert
    }

    @Test
    void wontGetTodaysUpcomingAlert() {
        createTestLocation()

        // Alert will only trigger in one minute
        def someAlert = someAlert(brisbaneTimeWithDeviation(0, 1))
        underTest.save(someAlert)

        def triggered = underTest.getTriggeredAlerts()
        assert triggered.size() == 0
    }

    @Test
    void wontGetPostponedForTomorrowAlerts() {
        createTestLocation()

        // Alert should have triggered a couple of minutes ago, but we'll postpone it for tomorrow
        def alert1 = underTest.save(someAlert(brisbaneTimeWithDeviation(0, -2)))
        def alert2 = underTest.save(someAlert(brisbaneTimeWithDeviation(0, -1)))
        assert underTest.getTriggeredAlerts().size() == 2

        // when
        underTest.postponeAlertsForTomorrow([alert1, alert2])

        // then
        def triggered = underTest.getTriggeredAlerts()
        assert triggered.size() == 0
    }

    @Test
    void tomorrowPostponeWithTwoAlerts() {
        createTestLocation()

        def alertToPostpone = someAlert(brisbaneTimeWithDeviation(0, -1))
        underTest.save(alertToPostpone)

        def alertToProcess = underTest.save(someAlert(brisbaneTimeWithDeviation(0, -1)))

        // both alerts will trigger
        assert underTest.getTriggeredAlerts().size() == 2

        // now we'll postpone one alert
        underTest.postponeAlertsForTomorrow([alertToPostpone])

        // second alert will still trigger
        def triggered = underTest.getTriggeredAlerts()
        assert triggered.size() == 1
        assert triggered[0] == alertToProcess
    }

    void createTestLocation() {
        template.update("INSERT INTO t_locations (code, lat, lng, timezone) values ('${CITY_CODE}', 12, 15, '${BRISBANE_TIMEZONE}')")
    }

    static WeatherAlert someAlert(LocalTime targetTime = LocalTime.of(8,0)) {
        return [
                chatId          : 123L,
                weatherType     : WeatherType.RAIN,
                locationLabel   : 'city',
                locationCode    : CITY_CODE,
                localAlertTime  : targetTime
        ] as WeatherAlert
    }

}
