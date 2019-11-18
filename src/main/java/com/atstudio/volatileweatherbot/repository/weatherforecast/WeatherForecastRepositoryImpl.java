package com.atstudio.volatileweatherbot.repository.weatherforecast;

import com.atstudio.volatileweatherbot.models.domain.Location;
import com.atstudio.volatileweatherbot.models.domain.forecast.ForecastDetails;
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;
import com.atstudio.volatileweatherbot.repository.AbstractJdbcRepository;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.atstudio.volatileweatherbot.repository.columns.EntityColumns.colName;
import static com.atstudio.volatileweatherbot.repository.columns.EntityColumnsUtils.joinColumnNames;
import static com.atstudio.volatileweatherbot.repository.weatherforecast.WeatherForecastColumns.*;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

@Repository
@Transactional
public class WeatherForecastRepositoryImpl extends AbstractJdbcRepository<WeatherForecast> implements WeatherForecastRepository {

    private static final Gson SERIALIZER = new GsonBuilder().create();

    @Override
    public WeatherForecast storeForecast(WeatherForecast forecast) {
        forecast.setUuid(generateUuid());
        createIfNotExist(forecast, WEATHER_FORECAST_TABLE, WeatherForecastColumns.values());
        storeDetails(forecast.getDetails(), forecast.getUuid());
        return forecast;
    }

    private void storeDetails(List<ForecastDetails> details, String forecastUuid) {
        if (isEmpty(details)) {
            return;
        }

        SqlParameterSource[] batchParamSource = details.stream()
                .map(detail -> paramSource(
                        ImmutableMap.<String, Object>builder()
                                .put(DETAILS_FORECAST_UUID_COL, forecastUuid)
                                .put(DETAILS_SERIALIZED_VALUE_COL, SERIALIZER.toJson(detail))
                                .build()
                        )
                )
                .toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(
                format("INSERT INTO %1$s (%2$s, %3$s) VALUES (:%2$s,:%3$s)", FORECAST_DETAILS_TABLE, DETAILS_FORECAST_UUID_COL, DETAILS_SERIALIZED_VALUE_COL),
                batchParamSource
        );

    }


    @Override
    public WeatherForecast getLatestLocationForecastForLocalTime(Location location, LocalDateTime dateTime) {
        String forecastColumns = joinColumnNames(",", "wf.", values());
        String detailsColumns = Stream.of(DETAILS_FORECAST_UUID_COL, DETAILS_SERIALIZED_VALUE_COL)
                                        .map(col -> "fd." + col).collect(joining(","));
        String joinedQuery = "SELECT " + forecastColumns + ", " + detailsColumns + " \n " +
                " FROM " + WEATHER_FORECAST_TABLE + " wf LEFT JOIN " + FORECAST_DETAILS_TABLE + " fd ON " +
                " wf." + colName(UUID) + " = fd." + DETAILS_FORECAST_UUID_COL + " \n " +
                " WHERE wf." + colName(LOCATION_CODE) + " = :loc_code" +
                "       AND :date_time BETWEEN wf." + colName(PERIOD_START) + " AND wf." + colName(PERIOD_END) +
                "       AND " + colName(UPDATE_TIME) + " = (" +
                "           SELECT MAX(" + colName(UPDATE_TIME) + ") FROM " + WEATHER_FORECAST_TABLE +
                "           WHERE " + colName(LOCATION_CODE) + " = :loc_code" +
                "       )";

        return jdbcTemplate.query(
                joinedQuery,
                paramSource(ImmutableMap.<String, Object>builder()
                        .put("loc_code", location.getCode())
                        .put("date_time", dateTime)
                        .build()
                ),
                extractor()
        );
    }

    private static ResultSetExtractor<WeatherForecast> extractor() {
        return rs -> {
            WeatherForecast forecast = null;
            List<ForecastDetails> details = new ArrayList<>();
            while (rs.next()) {
                if (forecast == null) {
                    forecast = new WeatherForecast();
                    for (WeatherForecastColumns columns: WeatherForecastColumns.values()) {
                        columns.setProp(forecast, rs);
                    }
                }
                details.add(
                        SERIALIZER.fromJson(
                                rs.getString(DETAILS_SERIALIZED_VALUE_COL),
                                ForecastDetails.class
                        )
                );
                if (!equalsIgnoreCase(rs.getString(DETAILS_FORECAST_UUID_COL), forecast.getUuid())) {
                    throw new IllegalStateException("Extracted more that one forecast. Verify your SQL");
                }
            }
            if (forecast == null) {
                return null;
            }
            forecast.setDetails(details);
            return forecast;
        };
    }
}
