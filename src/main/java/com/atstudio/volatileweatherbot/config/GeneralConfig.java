package com.atstudio.volatileweatherbot.config;

import com.atstudio.volatileweatherbot.models.dto.AlertInitDto;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Configuration
public class GeneralConfig {

    private final Environment environment;

    private Integer threadCount;

    @Autowired
    public GeneralConfig(Environment environment) {
        this.environment = environment;
        this.threadCount = environment.getProperty("main.executor.threads.count", Integer.class);
    }

    @Bean
    public TaskExecutor updateExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadCount);
        executor.setMaxPoolSize(threadCount);
        executor.setThreadNamePrefix("update_executor_thread");
        executor.initialize();
        return executor;
    }

    @Bean
    public Cache<Long, AlertInitDto> subscriptionCache() {
        return CacheBuilder.newBuilder()
                .concurrencyLevel(threadCount)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("messages");
        return source;
    }

    @Bean
    public Gson prettyGson() {
        GsonBuilder builder = new GsonBuilder();
        if (isTrue(
                environment.getProperty("gson.logs.pretty.print", Boolean.class))
        ) {
            builder.setPrettyPrinting();
        }
        return builder.create();
    }

}
