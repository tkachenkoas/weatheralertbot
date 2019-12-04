package com.atstudio.volatileweatherbot.services.scheduled.dao;

import com.atstudio.volatileweatherbot.models.domain.Location;

import java.util.Set;

public interface ForecastRefreshDao {
    Set<Location> getLocationsForForecastRefresh();
}