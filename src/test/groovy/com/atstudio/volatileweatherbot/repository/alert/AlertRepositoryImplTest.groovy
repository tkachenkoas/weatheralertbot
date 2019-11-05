package com.atstudio.volatileweatherbot.repository.alert

import com.atstudio.volatileweatherbot.models.WeatherAlert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
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
    void willSave() {
        WeatherAlert alert = [
                chatId: 123L,
                cityCode: 'city',
                lat: 123.56,
                lng: -54.321
        ] as WeatherAlert

        underTest.save(alert)

        def result = template.queryForMap("SELECT chat_id, city_code, lat, lng from t_weather_alerts");
        assert result.entrySet().size() == 4
    }

}
