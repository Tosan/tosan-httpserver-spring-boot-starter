package com.tosan.http.server.starter.aspect;

import com.tosan.http.server.starter.annotation.Timer;
import com.tosan.http.server.starter.util.StatisticsUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author AmirHossein ZamanZade
 * @since 10/22/2022
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StatisticsAspect extends BaseAspect {

    private final StatisticsUtil statisticsUtil;

    public StatisticsAspect(StatisticsUtil statisticsUtil) {
        this.statisticsUtil = statisticsUtil;
    }

    @Around(value = "@annotation(com.tosan.http.server.starter.annotation.Timer)")
    public Object calculateStatistics(ProceedingJoinPoint pjp) throws Throwable {
        Timer timer = getAnnotation(pjp, Timer.class);
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = pjp.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            statisticsUtil.generateStatistics(timer.serviceType(), timer.serviceName(), startTime, endTime);
        }
        return result;
    }
}
