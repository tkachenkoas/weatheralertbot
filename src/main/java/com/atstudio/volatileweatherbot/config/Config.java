package com.atstudio.volatileweatherbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class Config {

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("messages");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }

    @Bean
    public Gson prettyGsonPrinter() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

}
