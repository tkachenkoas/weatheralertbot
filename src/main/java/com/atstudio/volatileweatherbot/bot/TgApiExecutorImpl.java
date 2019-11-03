package com.atstudio.volatileweatherbot.bot;

import com.google.gson.Gson;
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
    private final Gson gson;

    @Autowired
    public TgApiExecutorImpl(@Lazy VolatileWeatherBot delegate, Gson gson) {
        this.delegate = delegate;
        this.gson = gson;
    }

    @Override
    @SneakyThrows
    public <Result extends Serializable, Method extends BotApiMethod<Result>> Result execute(Method method) {
        log.info("Outgoing method: {}", gson.toJson(method));
        Result result = delegate.execute(method);
        log.info("Response: {}", gson.toJson(result));
        return result;
    }
}
