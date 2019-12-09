package com.atstudio.volatileweatherbot.services.external.weather;

import com.atstudio.volatileweatherbot.models.domain.WeatherType;
import com.atstudio.volatileweatherbot.models.domain.Location;
import com.atstudio.volatileweatherbot.models.domain.forecast.ForecastDetails;
import com.atstudio.volatileweatherbot.models.domain.forecast.RainInfo;
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;
import org.openweathermap.api.model.MainParameters;
import org.openweathermap.api.model.Rain;
import org.openweathermap.api.model.forecast.ForecastInformation;
import org.openweathermap.api.model.forecast.hourly.HourlyForecast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static java.time.Instant.now;
import static java.time.ZoneOffset.ofTotalSeconds;
import static java.util.stream.Collectors.toList;

@Component
public class OpenWeatherMapForecastProvider implements WeatherForecastProvider {

    private final OpenWeatherMapApiAccessor apiAccessor;

    @Autowired
    public OpenWeatherMapForecastProvider(OpenWeatherMapApiAccessor apiAccessor) {
        this.apiAccessor = apiAccessor;
    }

    @Override
    public WeatherForecast getClosestForecastForLocation(Location location) {
        WeatherForecast forecast = new WeatherForecast();
        forecast.setLocationCode(location.getCode());

        ForecastInformation<HourlyForecast> forecastInfo = apiAccessor.getHourlyForecast(location);

        Instant now = Instant.now();
        int systemOffset = ZoneId.systemDefault().getRules().getOffset(now).getTotalSeconds();
        ZoneId timeZone = ofTotalSeconds(
                location.getTimeZone().getRules().getOffset(now).getTotalSeconds() + systemOffset
        );
        List<ForecastDetails> details = forecastInfo.getForecasts().stream()
                .map((HourlyForecast hourly) -> fromHourlyForecast(hourly, timeZone))
                .collect(toList());

        forecast.setDetails(details);
        forecast.setPeriodStart(details.get(0).getTargetDateTime());
        forecast.setPeriodEnd(details.get(details.size() - 1).getTargetDateTime());
        forecast.setUpdateTime(now());

        return forecast;
    }

    private ForecastDetails fromHourlyForecast(HourlyForecast forecast, ZoneId timeZone) {
        ForecastDetails details = new ForecastDetails();
        MainParameters mainPrms = forecast.getMainParameters();
        details.setTemperature(withTwoDigits(mainPrms.getTemperature()));
        details.setTemperatureDeviation(
                withTwoDigits(
                        (mainPrms.getMaximumTemperature() - mainPrms.getMinimumTemperature()) / 2
                )
        );
        details.setTargetDateTime(asLocalDateTime(forecast.getCalculationDate(), timeZone));
        Rain rain = forecast.getRain();
        if (rain != null) {
            details.setRainInfo(
                    new RainInfo(
                            withTwoDigits(rain.getThreeHours()),
                            forecast.getWeather().get(0).getDescription()
                    )
            );
            details.setExpectedWeatherType(WeatherType.RAIN);
        } else {
            details.setExpectedWeatherType(WeatherType.OTHER);
        }
        return details;
    }

    private BigDecimal withTwoDigits(double number) {
        return new BigDecimal(number).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private static LocalDateTime asLocalDateTime(Date date, ZoneId timeZone) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(timeZone)
                .toLocalDateTime();
    }
}
