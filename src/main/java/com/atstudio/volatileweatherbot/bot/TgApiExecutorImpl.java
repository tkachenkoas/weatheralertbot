package com.atstudio.volatileweatherbot.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;

@Service
@Slf4j
public class TgApiExecutorImpl implements TgApiExecutor {

    private final VolatileWeatherBot delegate;

    @Autowired
    public TgApiExecutorImpl(@Lazy VolatileWeatherBot delegate) {
        this.delegate = delegate;
    }

    @Override
    @SneakyThrows
    public <Result extends Serializable, Method extends BotApiMethod<Result>> Result execute(Method method) {
        log.info("Outgoing method: {}", method);
        Result result = delegate.execute(method);
        log.info("Response: {}", result);
        return result;
    }
}
