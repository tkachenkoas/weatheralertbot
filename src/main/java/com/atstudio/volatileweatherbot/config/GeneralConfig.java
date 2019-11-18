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

import static java.lang.Math.max;

@Configuration
public class GeneralConfig {

    private final Environment environment;

    @Autowired
    public GeneralConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean(name = "update")
    public TaskExecutor updateExecutor() {
        Integer updateThreads = environment.getProperty("main.executor.threads.count", Integer.class, 2);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(max(updateThreads / 2, 1));
        executor.setMaxPoolSize(updateThreads);
        executor.setThreadNamePrefix("update_executor_thread");
        executor.initialize();
        return executor;
    }

    @Bean(name = "async")
    public TaskExecutor asyncExecutor() {
        Integer asyncThreads = environment.getProperty("async.executor.threads.count", Integer.class, 4);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(max(asyncThreads / 2, 1));
        executor.setMaxPoolSize(asyncThreads);
        executor.setThreadNamePrefix("async_executor_thread");
        executor.initialize();
        return executor;
    }

    @Bean
    public Cache<Long, AlertInitDto> subscriptionCache() {
        Integer updateThreads = environment.getProperty("main.executor.threads.count", Integer.class, 2);

        return CacheBuilder.newBuilder()
                .concurrencyLevel(updateThreads)
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
        if (environment.getProperty("gson.logs.pretty.print", Boolean.class, false)) {
            builder.setPrettyPrinting();
        }
        return builder.create();
    }

}
