package com.atstudio.volatileweatherbot.config;

import com.atstudio.volatileweatherbot.models.dto.AlertInitDto;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.TimeUnit;

@Configuration
public class Config {

    @Value("${threads.count}")
    private Integer threadCount;

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
        return new GsonBuilder().setPrettyPrinting().create();
    }

}
