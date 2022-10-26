package com.tosan.http.server.starter.aspect;

import com.tosan.http.server.starter.annotation.Timer;
import com.tosan.http.server.starter.util.AspectUtil;
import com.tosan.http.server.starter.util.TimerStatisticsUtil;
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
public class TimerStatisticsAspect {

    private final TimerStatisticsUtil timerStatisticsUtil;
    private final AspectUtil aspectUtil;

    public TimerStatisticsAspect(TimerStatisticsUtil timerStatisticsUtil, AspectUtil aspectUtil) {
        this.timerStatisticsUtil = timerStatisticsUtil;
        this.aspectUtil = aspectUtil;
    }

    @Around(value = "@annotation(com.tosan.http.server.starter.annotation.Timer)")
    public Object calculateStatistics(ProceedingJoinPoint pjp) throws Throwable {
        Timer timer = aspectUtil.getAnnotation(pjp, Timer.class);
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = pjp.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            timerStatisticsUtil.generateStatistics(timer.serviceType(), timer.serviceName(), startTime, endTime);
        }
        return result;
    }
}
