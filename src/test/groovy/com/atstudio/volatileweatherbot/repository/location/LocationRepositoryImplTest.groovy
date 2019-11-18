package com.atstudio.volatileweatherbot.repository.location

import com.atstudio.volatileweatherbot.models.domain.Location
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert
import com.atstudio.volatileweatherbot.repository.RepoConfig
import com.atstudio.volatileweatherbot.repository.weatheralert.AlertRepositoryImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.LOCATIONS_TABLE_NAME
import static com.atstudio.volatileweatherbot.repository.weatheralert.AlertRepositoryImplTest.someAlert
import static com.atstudio.volatileweatherbot.repository.weatheralert.WeatherAlertColumns.WEATHER_ALERTS_TABLE
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTableWhere
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables

@ContextConfiguration(classes = RepoConfig)
@Import([AlertRepositoryImpl, LocationRepositoryImpl])
class LocationRepositoryImplTest extends AbstractTestNGSpringContextTests {

    @Autowired AlertRepositoryImpl alertRepository
    @Autowired LocationRepositoryImpl underTest

    @Autowired JdbcTemplate template

    @BeforeMethod
    @AfterMethod
    void cleanDb() {
        deleteFromTables(template, WEATHER_ALERTS_TABLE, LOCATIONS_TABLE_NAME)
    }

    @Test
    void locationIsStored() {
        Location loc = new Location('code', 10.0 as BigDecimal, 15.0 as BigDecimal)

        underTest.createIfNotExists(loc)
        assert countRowsInTableWhere(
                template,
                LOCATIONS_TABLE_NAME,
                "code = '${loc.getCode()}' and lat=${loc.getLat()} and lng=${loc.getLng()}") == 1
    }

    @Test
    void onCodeMatchWillDoNothing() {
        Location loc = new Location('code', 10.0 as BigDecimal, 15.0 as BigDecimal)
        underTest.createIfNotExists(loc)

        underTest.createIfNotExists(new Location('code', 15.0 as BigDecimal, 10.0 as BigDecimal))

        assert countRowsInTableWhere(
                template,
                LOCATIONS_TABLE_NAME,
                "code = '${loc.getCode()}' and lat=${loc.getLat()} and lng=${loc.getLng()}") == 1
    }

    @Test
    void willFindLocationsWithAlerts() {
        Location loc = new Location('code', 10.0 as BigDecimal, 15.0 as BigDecimal)
        underTest.createIfNotExists(loc)

        assert underTest.getLocationsWithActiveAlerts().size() == 0

        WeatherAlert alert = someAlert();
        alert.setLocationCode(loc.getCode())
        alertRepository.save(alert)

        def locations = underTest.getLocationsWithActiveAlerts()

        assert locations.size() == 1

        def actual = locations[0]
        assert actual.getCode() == loc.getCode() &&
                strip(actual.getLat()) == strip(loc.getLat()) &&
                strip(actual.getLng()) == strip(loc.getLng())
    }

    private BigDecimal strip(BigDecimal src) {
        return src.stripTrailingZeros()
    }

}
