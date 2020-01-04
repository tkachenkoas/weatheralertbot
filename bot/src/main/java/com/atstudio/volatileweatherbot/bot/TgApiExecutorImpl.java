package com.atstudio.volatileweatherbot.bot;

import com.atstudio.volatileweatherbot.aspect.LogArgsAndResult;
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
    @LogArgsAndResult
    public <Result extends Serializable, Method extends BotApiMethod<Result>> Result execute(Method method) {
        return delegate.execute(method);
    }
}
