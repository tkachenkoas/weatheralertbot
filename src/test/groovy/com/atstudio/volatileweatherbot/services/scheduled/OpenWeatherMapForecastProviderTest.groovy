package com.atstudio.volatileweatherbot.services.scheduled

import com.atstudio.volatileweatherbot.TestJsonHelper
import com.atstudio.volatileweatherbot.models.domain.AlertWeatherType
import com.atstudio.volatileweatherbot.models.domain.Location
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast
import com.atstudio.volatileweatherbot.services.external.weather.OpenWeatherMapApiAccessor
import com.atstudio.volatileweatherbot.services.external.weather.OpenWeatherMapForecastProvider
import org.mockito.Mock
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.time.Duration
import java.time.Instant
import java.time.Month

import static java.time.LocalDateTime.of
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.when
import static org.mockito.MockitoAnnotations.initMocks

class OpenWeatherMapForecastProviderTest {

    OpenWeatherMapForecastProvider underTest
    @Mock OpenWeatherMapApiAccessor accessor

    @BeforeMethod
    void init() {
        initMocks(this)
        underTest = new OpenWeatherMapForecastProvider(accessor)
    }

    @Test
    void testConvert() {
        Location testLocation = new Location('city', 10.0 as BigDecimal, 15.0 as BigDecimal)

        when(accessor.getHourlyForecast(eq(testLocation)))
                .thenReturn(TestJsonHelper.getWeatherForecast('clouds-rain.json'))

        WeatherForecast forecast = underTest.getClosestForecastForLocation(testLocation)

        assert forecast.getPeriodStart() == of(2019, Month.NOVEMBER, 14, 7, 00, 00)
        assert forecast.getPeriodEnd() == of(2019, Month.NOVEMBER, 14, 22, 00, 00)

        assert forecast.getLocationCode() == 'city'
        assert Duration.between(forecast.getUpdateTime(), Instant.now()).getSeconds() < 1

        def details = forecast.getDetails()
        assert details.size() == 6

        def first = details[0]

        assert first.getTargetDateTime() == of(2019, Month.NOVEMBER, 14, 7, 00, 00)
        assert first.getExpectedWeatherType() == AlertWeatherType.OTHER

        assert first.getRainInfo() == null

        def last = details[5]
        assert last.getTemperature() == 22.78
        assert last.getTemperatureDeviation() == 0

        assert last.getTargetDateTime() == of(2019, Month.NOVEMBER, 14, 22, 00, 00)
        assert last.getExpectedWeatherType() == AlertWeatherType.RAIN

        def rain = last.getRainInfo()
        assert rain.getExpectedAmount() == 0.13
        assert rain.getDescription() == 'light rain'
    }


}
