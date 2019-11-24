package com.atstudio.volatileweatherbot.repository;

import java.util.UUID;

public class RepoJdbcUtils {

    private RepoJdbcUtils() {
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

}
