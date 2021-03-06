package com.atstudio.volatileweatherbot.aspect;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogArgumentsAspect {

    private final Gson gson;

    @Autowired
    public LogArgumentsAspect(Gson gson) {
        this.gson = gson;
    }

    @Before("@annotation(com.atstudio.volatileweatherbot.aspect.LogArgsAndResult)")
    public void logArguments(JoinPoint joinPoint) throws Throwable {
        log.info("Executing method {}, method arguments are: ", joinPoint.toShortString());
        int index = 0;
        for (Object arg: joinPoint.getArgs()) {
            log.info("Arg #{}: {}", ++index, asLogString(arg));
        }
    }

    @AfterReturning(
            value = "@annotation(com.atstudio.volatileweatherbot.aspect.LogArgsAndResult)",
            returning = "result"
    )
    public void logResult(JoinPoint joinPoint, Object result) {
        if (result == null) {
            return;
        }
        log.info("Method execution result is {}", asLogString(result));
    }

    private String asLogString(Object obj) {
        return gson.toJson(obj);
    }

}
