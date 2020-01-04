package com.atstudio.volatileweatherbot.services.scheduled.service.weatheralert;

import com.atstudio.volatileweatherbot.models.domain.WeatherType;
import com.atstudio.volatileweatherbot.models.domain.WeatherAlert;
import com.atstudio.volatileweatherbot.models.domain.forecast.ForecastDetails;
import com.atstudio.volatileweatherbot.models.domain.forecast.WeatherForecast;
import com.atstudio.volatileweatherbot.services.util.BotMessageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.join;

@Component
public class RainForecastToAlertMatchProcessor implements ForecastToAlertMatchProcessor {

    private final BotMessageProvider messageProvider;

    @Autowired
    public RainForecastToAlertMatchProcessor(BotMessageProvider messageProvider) {
        this.messageProvider = messageProvider;
    }

    @Override
    public void checkCurrentForecastForAlertMatch(WeatherAlert alert, WeatherForecast currentForecast) {
        if (alert.getWeatherType() != WeatherType.RAIN) {
            return;
        }
        List<String> hourlyRainAmount = new ArrayList<>();
        for (ForecastDetails details : currentForecast.getDetails()) {
            if (details.getExpectedWeatherType() != WeatherType.RAIN) {
                continue;
            }

            hourlyRainAmount.add(
                    messageProvider.getMessageWithArgs(
                            "rain-info",
                            details.getTargetDateTime().toLocalTime(),
                            details.getRainInfo().getExpectedAmount()
                    )
            );
        }
        if (hourlyRainAmount.isEmpty()) {
            return;
        }
        String alertMessage = messageProvider.getMessage("rain-info-header") + ": " + join("; ", hourlyRainAmount);
        ChatAlertContext.addChatMessageForLocation(alert.getChatId(), alert.getLocationLabel(), alertMessage);
    }
}
