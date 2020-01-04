package com.atstudio.volatileweatherbot.repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class RepoJdbcUtils {

    private RepoJdbcUtils() {
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    public static Timestamp toTimeStamp(Instant instant) {
        return new Timestamp(instant.toEpochMilli());
    }

}
