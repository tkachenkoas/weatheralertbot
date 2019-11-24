package com.atstudio.volatileweatherbot.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.Map;
import java.util.UUID;

public class RepoJdbcUtils {

    private RepoJdbcUtils() {
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    public static SqlParameterSource paramSource(Map<String, Object> values) {
        return new MapSqlParameterSource(values);
    }
}
