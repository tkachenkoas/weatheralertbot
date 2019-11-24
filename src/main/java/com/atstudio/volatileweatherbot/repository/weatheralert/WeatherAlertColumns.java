package com.atstudio.volatileweatherbot.repository.weatheralert;

import com.atstudio.volatileweatherbot.models.domain.AlertWeatherType;
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;
import com.atstudio.volatileweatherbot.repository.columns.EntityColumns;
import com.atstudio.volatileweatherbot.repository.columns.PropSetter;
import lombok.Getter;

import java.util.function.Function;

@Getter
public enum WeatherAlertColumns implements EntityColumns<WeatherAlert> {
    CHAT_ID("chat_id", WeatherAlert::getChatId, (obj, rs, col) -> obj.setChatId(rs.getLong(col))),
    ALERT_TYPE("alert_type",
            alert -> alert.getAlertWeatherType().name(),
            (obj, rs, col) -> obj.setAlertWeatherType(AlertWeatherType.valueOf(rs.getString(col)))
    ),
    ALERT_LOCATION_CODE("location_code", WeatherAlert::getLocationCode, (obj, rs, col) -> obj.setLocationCode(rs.getString(col))),
    LOCATION_LABEL("location_label", WeatherAlert::getLocationLabel, (obj, rs, col) -> obj.setLocationLabel(rs.getString(col))),
    ALERT_TIME("alert_time",
            WeatherAlert::getLocalAlertTime,
            (obj, rs, col) -> obj.setLocalAlertTime(rs.getTimestamp(col).toLocalDateTime().toLocalTime())
    );

    public static final String WEATHER_ALERTS_TABLE = "t_weather_alerts";

    private final String colName;
    private final Function<WeatherAlert, Object> propAccessor;
    private final PropSetter<WeatherAlert> propSetter;

    WeatherAlertColumns(String colName, Function<WeatherAlert, Object> propAccessor, PropSetter<WeatherAlert> propSetter) {
        this.colName = colName;
        this.propAccessor = propAccessor;
        this.propSetter = propSetter;
    }

}
