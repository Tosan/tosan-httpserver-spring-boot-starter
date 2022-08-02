package com.tosan.http.server.starter.aspect;

import com.tosan.http.server.starter.logger.JsonServiceLogger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

/**
 * @author mina khoshnevisan
 * @since 7/25/2022
 */
@Order(1)
@Aspect
public class ServiceLogAspect {

    private final JsonServiceLogger jsonServiceLogger;

    public ServiceLogAspect(JsonServiceLogger jsonServiceLogger) {
        this.jsonServiceLogger = jsonServiceLogger;
    }

    @Around(value = "execution(* (@org.springframework.web.bind.annotation.RequestMapping *).*(..))")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        return jsonServiceLogger.log(pjp);
    }
}