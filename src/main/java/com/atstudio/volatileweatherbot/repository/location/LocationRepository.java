package com.atstudio.volatileweatherbot.repository.location;

import com.atstudio.volatileweatherbot.models.domain.Location;

public interface LocationRepository {

    void createIfNotExists(Location location);
    Location getByCode(String locationCode);
}
