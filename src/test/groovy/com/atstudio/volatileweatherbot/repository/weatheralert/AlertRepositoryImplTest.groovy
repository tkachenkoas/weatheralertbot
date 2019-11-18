package com.atstudio.volatileweatherbot.repository.weatheralert

import com.atstudio.volatileweatherbot.models.domain.AlertWeatherType
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
    void willSaveAndRetrieveAlert() {
        template.update("INSERT INTO t_locations (code, lat, lng, timezone) values ('cityCode', 10, 15, 'Europe/Moscow')")

        WeatherAlert alert = someAlert()

        underTest.save(alert)

        List<WeatherAlert> stored = underTest.getForLocation('cityCode');

        assert stored.size() == 1
        assert stored[0] == alert
    }

    static WeatherAlert someAlert() {
        return [
                chatId          : 123L,
                alertWeatherType : AlertWeatherType.RAIN,
                locationLabel   : 'city',
                locationCode    : 'cityCode',
                localAlertTime  : LocalTime.of(8,0)
        ] as WeatherAlert
    }

}
