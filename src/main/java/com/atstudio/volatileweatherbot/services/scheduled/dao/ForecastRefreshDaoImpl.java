package com.atstudio.volatileweatherbot.services.scheduled.dao;

import com.atstudio.volatileweatherbot.models.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ForecastRefreshDaoImpl implements ForecastRefreshDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ForecastRefreshDaoImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Location> getLocationsForRefresh() {
        return null;
    }
}