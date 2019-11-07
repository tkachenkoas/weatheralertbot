package com.atstudio.volatileweatherbot.repository.location;

import com.atstudio.volatileweatherbot.models.domain.Location;
import com.atstudio.volatileweatherbot.repository.AbstractJdbcRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LocationRepositoryImpl extends AbstractJdbcRepository<Location> implements LocationRepository {
    @Override
    public void save(Location location) {
        saveEntity(location, LocationColumns.TABLE_NAME, LocationColumns.values());
    }

    @Override
    public List<Location> getLocationsWithActiveAlerts() {
        return null;
    }
}
