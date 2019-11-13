package com.atstudio.volatileweatherbot.services.scheduled

import com.atstudio.volatileweatherbot.TestJsonHelper
import com.atstudio.volatileweatherbot.models.domain.Location
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast
import org.mockito.Mock
import org.mockito.Mockito
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.time.Month

import static java.time.LocalDateTime.of
import static org.mockito.ArgumentMatchers.eq

class OpenWeatherMapForecastProviderTest {

    OpenWeatherMapForecastProvider underTest
    @Mock
    OpenWeatherMapApiAccessor accessor

    @BeforeMethod
    void init() {
        underTest = new OpenWeatherMapForecastProvider(accessor)
    }

    @Test
    void testConvert() {

        Location testLocation = new Location('city', 10.0 as BigDecimal, 15.0 as BigDecimal)

        Mockito.when(accessor.getCurrentWeather(eq(testLocation)))
                .thenReturn(TestJsonHelper.getWeatherForecast('clouds-rain.json'))

        WeatherForecast forecast = underTest.getClosestForecastForLocation(testLocation)

        assert forecast.getPeriodStart() == of(2019, Month.NOVEMBER, 13, 21, 00, 00)
        assert forecast.getPeriodEnd() == of(2019, Month.NOVEMBER, 14, 12, 00, 00)

        assert forecast.getLocationCode() == 'city'

        def details = forecast.getDetails()
        assert details.size() == 6

        def first = details[0]

        assert first.getFrom() == of(2019, Month.NOVEMBER, 13, 21, 00, 00)
        assert first.getFrom() == of(2019, Month.NOVEMBER, 14, 00, 00, 00)

        assert first.getRainInfo() == null

        def last = details[5]
        assert last.getTemperature() == 22.78
        assert last.getTemperatureDeviation() == 0

        assert last.getFrom() == of(2019, Month.NOVEMBER, 14, 9, 00, 00)
        assert last.getTo() == of(2019, Month.NOVEMBER, 14, 12, 00, 00)

        def rain = last.getRainInfo()
        assert rain.getExpectedAmount() == 0.13
        assert rain.getDescription() == 'light rain'



    }


}
