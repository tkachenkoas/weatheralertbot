package com.atstudio.volatileweatherbot.repository.weatherforecast;

import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;
import com.atstudio.volatileweatherbot.repository.columns.EntityColumns;
import com.atstudio.volatileweatherbot.repository.columns.PropSetter;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.function.Function;

@Getter
public enum WeatherForecastColumns implements EntityColumns<WeatherForecast> {
    UUID(
            "uuid",
            WeatherForecast::getUuid,
            (obj, rs, column) -> obj.setUuid(rs.getString(column))
    ),
    FORECAST_LOCATION_CODE(
            "location_code",
            WeatherForecast::getLocationCode,
            (obj, rs, column) -> obj.setLocationCode(rs.getString(column))
    ),
    PERIOD_START(
            "period_start",
            WeatherForecast::getPeriodStart,
            (obj, rs, column) -> obj.setPeriodStart(rs.getTimestamp(column).toLocalDateTime())
    ),
    PERIOD_END(
            "period_end",
            WeatherForecast::getPeriodEnd,
            (obj, rs, column) -> obj.setPeriodEnd(rs.getTimestamp(column).toLocalDateTime())
    ),
    UPDATE_TIME(
            "update_time",
            (obj) -> new Timestamp(obj.getUpdateTime().toEpochMilli()),
            (obj, rs, column) -> obj.setUpdateTime(rs.getTimestamp(column).toInstant())
    );

    public static final String WEATHER_FORECAST_TABLE = "t_weather_forecasts";

    public static final String FORECAST_DETAILS_TABLE = "t_forecast_details";
    public static final String DETAILS_FORECAST_UUID_COL = "forecast_uuid";
    public static final String DETAILS_SERIALIZED_VALUE_COL = "serialized_value";

    private final String colName;
    private final Function<WeatherForecast, Object> propAccessor;
    private final PropSetter<WeatherForecast> propSetter;

    WeatherForecastColumns(String colName, Function<WeatherForecast, Object> propAccessor, PropSetter<WeatherForecast> propSetter) {
        this.colName = colName;
        this.propAccessor = propAccessor;
        this.propSetter = propSetter;
    }
}
