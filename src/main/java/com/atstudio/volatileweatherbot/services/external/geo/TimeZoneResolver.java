package com.atstudio.volatileweatherbot.services.external.geo;

import java.math.BigDecimal;
import java.time.ZoneId;

public interface TimeZoneResolver {
    ZoneId timeZoneForCoordinates(BigDecimal lat, BigDecimal lng);
}
