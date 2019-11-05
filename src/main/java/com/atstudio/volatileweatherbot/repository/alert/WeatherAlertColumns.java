package com.atstudio.volatileweatherbot.repository.alert;

import com.atstudio.volatileweatherbot.models.WeatherAlert;
import com.atstudio.volatileweatherbot.repository.PropSetter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public enum WeatherAlertColumns {
    CHAT_ID("chat_id", WeatherAlert::getChatId, (obj, rs, col) -> obj.setChatId(rs.getLong(col))),
    CITY_CODE("city_code", WeatherAlert::getCityCode, (obj, rs, col) -> obj.setCityCode(rs.getString(col))),
    LAT("lat", WeatherAlert::getLat, (obj, rs, col) -> obj.setLat(rs.getBigDecimal(col))),
    LNG("lng", WeatherAlert::getLng, (obj, rs, col) -> obj.setLng(rs.getBigDecimal(col)));

    public static final String TABLE_NAME = "t_weather_alerts";

    private final String colName;
    private final Function<WeatherAlert, Object> getter;
    private final PropSetter<WeatherAlert> propSetter;
    WeatherAlertColumns(String colName, Function<WeatherAlert, Object> getter, PropSetter<WeatherAlert> propSetter) {
        this.colName = colName;
        this.getter = getter;
        this.propSetter = propSetter;
    }

    public String getColName() {
        return colName.toUpperCase();
    }

    public <T> T getProp(WeatherAlert alert) {
        return (T) getter.apply(alert);
    }

    public void setProp(WeatherAlert alert, ResultSet rs) throws SQLException {
        propSetter.setProp(alert, rs, colName);
    }

    public static String joinColumnNames(String delimeter, WeatherAlertColumns ... args) {
        return Stream.of(args)
                .map(WeatherAlertColumns::getColName)
                .collect(joining(delimeter));
    }

    public static Object[] getFieldsFromObject(WeatherAlert object, WeatherAlertColumns ... args) {
        return Stream.of(args)
                .map(col -> col.getProp(object))
                .toArray();
    }

}
