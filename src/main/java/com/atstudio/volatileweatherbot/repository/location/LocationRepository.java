package com.atstudio.volatileweatherbot.repository.location;

import com.atstudio.volatileweatherbot.models.domain.Location;

import java.util.List;

public interface LocationRepository {

    void createIfNotExists(Location location);
    List<Location> getLocationsWithActiveAlerts();
    
}
