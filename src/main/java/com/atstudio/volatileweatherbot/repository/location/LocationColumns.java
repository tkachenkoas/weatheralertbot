package com.atstudio.volatileweatherbot.repository.location;

import com.atstudio.volatileweatherbot.models.domain.Location;
import com.atstudio.volatileweatherbot.repository.columns.EntityColumns;
import com.atstudio.volatileweatherbot.repository.columns.PropSetter;
import lombok.Getter;

import java.util.function.Function;

@Getter
public enum LocationColumns implements EntityColumns<Location> {
    CODE("code", Location::getCode, (obj, rs, col) -> obj.setCode(rs.getString(col))),
    LAT("lat", Location::getLat, (obj, rs, col) -> obj.setLat(rs.getBigDecimal(col))),
    LNG("lng", Location::getLng, (obj, rs, col) -> obj.setLng(rs.getBigDecimal(col)));

    public static final String LOCATIONS_TABLE_NAME = "t_locations";

    private final String colName;
    private final Function<Location, Object> propAccessor;
    private final PropSetter<Location> propSetter;

    LocationColumns(String colName, Function<Location, Object> propAccessor, PropSetter<Location> propSetter) {
        this.colName = colName;
        this.propAccessor = propAccessor;
        this.propSetter = propSetter;
    }

}
