package com.atstudio.volatileweatherbot.bot

import com.atstudio.volatileweatherbot.BotApplication
import com.atstudio.volatileweatherbot.TestJsonHelper
import com.atstudio.volatileweatherbot.services.external.geo.TimeZoneResolver
import com.atstudio.volatileweatherbot.services.external.geo.googlemaps.GoogleApiAccessor
import com.atstudio.volatileweatherbot.services.external.weather.OpenWeatherMapApiAccessor
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc
import org.springframework.context.annotation.*
import org.springframework.test.context.ActiveProfiles
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

import java.time.ZoneId

import static com.atstudio.volatileweatherbot.TestJsonHelper.geocodingsFromFile
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

@Configuration
@ComponentScan(basePackages = "com.atstudio.volatileweatherbot", excludeFilters = [
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = BotApplication.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TgApiExecutorImpl.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = VolatileWeatherBot.class)
])
@AutoConfigureDataJdbc
@ActiveProfiles("integration-tests")
@PropertySource("classpath:test.properties")
class BotTestConfigExcludingTgBeans {

    @Bean
    TgApiExecutor executor(List<BotApiMethod> lastExecuted) {
        TgApiExecutor result = mock(TgApiExecutor)
        when(result.execute(any())).thenAnswer({ inv ->
            lastExecuted.add(inv.getArgument(0))
            return null;
        })
        return result
    }

    @Bean
    GoogleApiAccessor googleApiAccessor() {
        def mock = mock(GoogleApiAccessor)
        when(mock.getGeocodings(any()))
                .thenReturn(geocodingsFromFile('brisbane-location.json'))
        return mock
    }

    @Bean
    TimeZoneResolver resolver() {
        def resolver = mock(TimeZoneResolver)
        when(resolver.timeZoneForCoordinates(any(), any())).thenReturn(ZoneId.of("Australia/Brisbane"))
        return resolver
    }

    @Bean
    OpenWeatherMapApiAccessor openWeatherMapApiAccessor() {
        def mock = mock(OpenWeatherMapApiAccessor)
        when(mock.getHourlyForecast(any()))
                .thenReturn(TestJsonHelper.getWeatherForecast('clouds-rain.json'))
        return mock
    }

    @Bean
    List<BotApiMethod> executedMethods() {
        return [] as LinkedList
    }

}