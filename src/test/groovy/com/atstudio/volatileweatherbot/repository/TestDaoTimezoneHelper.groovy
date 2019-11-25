package com.atstudio.volatileweatherbot.repository

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

import static java.time.ZoneId.systemDefault
import static java.util.concurrent.TimeUnit.HOURS
import static java.util.concurrent.TimeUnit.MINUTES

class TestDaoTimezoneHelper {

    public static String BRISBANE_TIMEZONE = 'Australia/Brisbane'
    public static String CITY_CODE = 'CITY_CODE'

    static LocalTime brisbaneTimeWithDeviation(int hrs = 1, int minutes = 0) {
        Instant now = Instant.now()
        def secondsOffset = { ZoneId zone -> zone.getOffset(now).getTotalSeconds() }
        long tzDeviation = secondsOffset(ZoneId.of(BRISBANE_TIMEZONE)) - secondsOffset(systemDefault())
        return LocalTime.now().plus(tzDeviation + HOURS.toSeconds(hrs) + MINUTES.toSeconds(minutes), ChronoUnit.SECONDS)
    }

}
