package com.atstudio.volatileweatherbot.bot

import com.atstudio.volatileweatherbot.BotApplication
import com.atstudio.volatileweatherbot.services.external.googlemaps.GoogleApiAccessor
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc
import org.springframework.context.annotation.*
import org.springframework.test.context.ActiveProfiles
import org.telegram.telegrambots.meta.api.methods.BotApiMethod

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
        when(result.execute(any())).thenAnswer({ inv  ->
            lastExecuted.add(inv.getArgument(0))
            return null;
        })
        return result
    }

    @Bean
    GoogleApiAccessor googleApiAccessor() {
        def mock = mock(GoogleApiAccessor)
        when(mock.getGeocodings(any()))
                .thenReturn(geocodingsFromFile('single-result.json'))
        return mock
    }

    @Bean
    List<BotApiMethod> executedMethods() {
        return [] as LinkedList
    }

}