package com.tosan.http.server.starter.aspect;

import com.tosan.http.server.starter.annotation.Timer;
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
public class TimerStatisticsAspect extends BaseAspect {

    private final TimerStatisticsUtil timerStatisticsUtil;

    public TimerStatisticsAspect(TimerStatisticsUtil timerStatisticsUtil) {
        this.timerStatisticsUtil = timerStatisticsUtil;
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
            timerStatisticsUtil.generateStatistics(timer.serviceType(), timer.serviceName(), startTime, endTime);
        }
        return result;
    }
}
