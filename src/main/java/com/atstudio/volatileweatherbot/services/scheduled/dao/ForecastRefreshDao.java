package com.atstudio.volatileweatherbot.services.scheduled.dao;

import com.atstudio.volatileweatherbot.models.domain.Location;

import java.util.List;

public interface ForecastRefreshDao {
    List<Location> getLocationsForForecastRefresh();
}