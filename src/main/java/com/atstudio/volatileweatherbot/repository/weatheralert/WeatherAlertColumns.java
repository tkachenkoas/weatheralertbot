package com.atstudio.volatileweatherbot.repository.weatheralert;

import com.atstudio.volatileweatherbot.models.domain.WeatherType;
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;
import com.atstudio.volatileweatherbot.repository.columns.EntityColumns;
import com.atstudio.volatileweatherbot.repository.columns.PropSetter;
import lombok.Getter;

import java.util.function.Function;

@Getter
public enum WeatherAlertColumns implements EntityColumns<WeatherAlert> {
    UUID("uuid", WeatherAlert::getUuid, (obj, rs, col) -> obj.setUuid(rs.getString(col))),
    CHAT_ID("chat_id", WeatherAlert::getChatId, (obj, rs, col) -> obj.setChatId(rs.getLong(col))),
    ALERT_TYPE("alert_type",
            alert -> alert.getWeatherType().name(),
            (obj, rs, col) -> obj.setWeatherType(WeatherType.valueOf(rs.getString(col)))
    ),
    ALERT_LOCATION_CODE("location_code", WeatherAlert::getLocationCode, (obj, rs, col) -> obj.setLocationCode(rs.getString(col))),
    LOCATION_LABEL("location_label", WeatherAlert::getLocationLabel, (obj, rs, col) -> obj.setLocationLabel(rs.getString(col))),
    ALERT_TIME("alert_time",
            WeatherAlert::getLocalAlertTime,
            (obj, rs, col) -> obj.setLocalAlertTime(rs.getTimestamp(col).toLocalDateTime().toLocalTime())
    );

    public static final String WEATHER_ALERTS_TABLE = "t_weather_alerts";
    public static final String NEXT_CHECK_DATE_COLUMN = "next_check_date";

    private final String colName;
    private final Function<WeatherAlert, Object> propAccessor;
    private final PropSetter<WeatherAlert> propSetter;

    WeatherAlertColumns(String colName, Function<WeatherAlert, Object> propAccessor, PropSetter<WeatherAlert> propSetter) {
        this.colName = colName;
        this.propAccessor = propAccessor;
        this.propSetter = propSetter;
    }

}
