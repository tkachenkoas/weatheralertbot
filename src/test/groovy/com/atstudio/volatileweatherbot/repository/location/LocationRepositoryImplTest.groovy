package com.atstudio.volatileweatherbot.repository.location

import com.atstudio.volatileweatherbot.models.domain.Location
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

import java.time.ZoneId

import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.LOCATIONS_TABLE_NAME
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
        deleteFromTables(template, LOCATIONS_TABLE_NAME)
    }

    @Test
    void locationIsStored() {
        Location loc = rndLocation()

        underTest.createIfNotExists(loc)
        assert countRowsInTableWhere(
                template,
                LOCATIONS_TABLE_NAME,
                "code = '${loc.getCode()}' and lat=${loc.getLat()} and lng=${loc.getLng()}") == 1
    }

    @Test
    void onCodeMatchWillDoNothing() {
        Location loc = rndLocation()
        underTest.createIfNotExists(loc)

        underTest.createIfNotExists(rndLocation())

        assert countRowsInTableWhere(
                template,
                LOCATIONS_TABLE_NAME,
                "code = '${loc.getCode()}' and lat=${loc.getLat()} and lng=${loc.getLng()}") == 1
    }

    @Test
    void willLoadStoredLocation() {
        Location loc = rndLocation()
        underTest.createIfNotExists(loc)

       assert loc == underTest.getByCode('code')
    }

    private Location rndLocation(String  code = 'code') {
        return new Location(code, 10.1 as BigDecimal, 15.2 as BigDecimal, ZoneId.systemDefault())
    }

}
