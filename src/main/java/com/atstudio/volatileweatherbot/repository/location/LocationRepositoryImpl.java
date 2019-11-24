package com.atstudio.volatileweatherbot.repository.location;

import com.atstudio.volatileweatherbot.models.domain.Location;
import com.atstudio.volatileweatherbot.repository.AbstractJdbcRepository;
import com.google.common.collect.ImmutableMap;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import static com.atstudio.volatileweatherbot.repository.RepoJdbcUtils.paramSource;
import static com.atstudio.volatileweatherbot.repository.columns.EntityColumns.colName;
import static com.atstudio.volatileweatherbot.repository.columns.EntityColumnsUtils.joinColumnNames;
import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.LOCATION_CODE;
import static com.atstudio.volatileweatherbot.repository.location.LocationColumns.LOCATIONS_TABLE_NAME;

@Repository
public class LocationRepositoryImpl extends AbstractJdbcRepository<Location> implements LocationRepository {

    @Override
    public void createIfNotExists(Location location) {
        createIfNotExist(location, LOCATIONS_TABLE_NAME, LocationColumns.values());
    }

    @Override
    public Location getByCode(String locationCode) {
        return jdbcTemplate.queryForObject(
                " SELECT " + joinColumnNames(",", LocationColumns.values()) +
                        " FROM " + LOCATIONS_TABLE_NAME +
                        " WHERE " + colName(LOCATION_CODE) + "= :code",
                paramSource(ImmutableMap.of("code", locationCode)),
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
