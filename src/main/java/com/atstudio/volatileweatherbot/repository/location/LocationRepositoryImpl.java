package com.atstudio.volatileweatherbot.repository.location;

import com.atstudio.volatileweatherbot.models.domain.Location;
import com.atstudio.volatileweatherbot.repository.AbstractJdbcRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.atstudio.volatileweatherbot.repository.columns.EntityColumnsUtils.joinColumnNames;
import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.CODE;
import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.LOCATIONS_TABLE_NAME;
import static com.atstudio.volatileweatherbot.repository.weatheralert.WeatherAlertColumns.LOCATION_CODE;
import static com.atstudio.volatileweatherbot.repository.weatheralert.WeatherAlertColumns.WEATHER_ALERTS_TABLE;

@Repository
public class LocationRepositoryImpl extends AbstractJdbcRepository<Location> implements LocationRepository {

    private static final String LOC_TABLE_PRFX = "loc.";

    @Override
    public void createIfNotExists(Location location) {
        createIfNotExist(location, LOCATIONS_TABLE_NAME, LocationColumns.values());
    }

    @Override
    public List<Location> getLocationsWithActiveAlerts() {
        return jdbcTemplate.query(
                " SELECT " + joinColumnNames(",", LOC_TABLE_PRFX, LocationColumns.values()) +
                        " FROM " + LOCATIONS_TABLE_NAME + " loc " +
                        " WHERE EXISTS(" +
                        "       SELECT 1 FROM " + WEATHER_ALERTS_TABLE + " alerts " +
                        "       WHERE alerts." + LOCATION_CODE.getColName() + " = " + LOC_TABLE_PRFX + CODE.getColName() +
                        ")",
                rowMapper
        );
    }

    private static RowMapper<Location> rowMapper = (resultSet, i) ->  {
        Location result = new Location();
        for (LocationColumns column: LocationColumns.values()) {
            column.setProp(result, resultSet);
        }
        return result;
    };
}
