package com.atstudio.volatileweatherbot.repository.weatheralert

import com.atstudio.volatileweatherbot.models.domain.AlertWeatherType
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert
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

@ContextConfiguration(classes = RepoConfig)
@Import(AlertRepositoryImpl)
class AlertRepositoryImplTest extends AbstractTestNGSpringContextTests {

    @Autowired JdbcTemplate template
    @Autowired AlertRepositoryImpl underTest

    @BeforeMethod
    @AfterMethod
    void cleanDb() {
        JdbcTestUtils.deleteFromTables(template, "t_weather_alerts")
        JdbcTestUtils.deleteFromTables(template, "t_locations")
    }

    @Test
    void willSaveAndRetrieveAlert() {
        template.update("INSERT into t_locations (code, lat, lng) values ('cityCode', 10, 15)")

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
                locationCode    : 'cityCode'
        ] as WeatherAlert
    }

}
