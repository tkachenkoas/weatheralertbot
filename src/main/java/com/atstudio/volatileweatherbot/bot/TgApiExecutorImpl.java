package com.atstudio.volatileweatherbot.bot;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;

@Service
public class TgApiExecutorImpl implements TgApiExecutor {

    private final VolatileWeatherBot delegate;

    @Autowired
    public TgApiExecutorImpl(VolatileWeatherBot delegate) {
        this.delegate = delegate;
    }

    @Override
    @SneakyThrows
    public <Result extends Serializable, Method extends BotApiMethod<Result>> Result execute(Method method) {
        return delegate.execute(method);
    }
}
