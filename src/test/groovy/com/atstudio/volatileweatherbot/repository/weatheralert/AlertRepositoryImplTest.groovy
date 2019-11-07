package com.atstudio.volatileweatherbot.repository.weatheralert

import com.atstudio.volatileweatherbot.models.domain.WeatherAlert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.jdbc.JdbcTestUtils
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@PropertySource("classpath:/test.properties")
@AutoConfigureDataJdbc
@Import(AlertRepositoryImpl)
class AlertRepositoryImplTest extends AbstractTestNGSpringContextTests {

    @Autowired JdbcTemplate template
    @Autowired AlertRepositoryImpl underTest

    @BeforeMethod
    void cleanDb() {
        JdbcTestUtils.deleteFromTables(template, "t_weather_alerts")
    }

    @Test
    void willSaveAndRetreiveAlert() {
        WeatherAlert alert = [
                chatId          : 123L,
                locationLabel   : 'city',
                locationCode    : 'cityCode'
        ] as WeatherAlert

        underTest.save(alert)

        List<WeatherAlert> stored = underTest.getForLocation('cityCode');

        assert stored.size() == 1
        assert stored[0] == alert
    }

}
