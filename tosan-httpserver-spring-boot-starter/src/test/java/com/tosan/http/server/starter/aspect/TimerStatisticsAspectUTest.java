package com.tosan.http.server.starter.aspect;

import com.tosan.http.server.starter.annotation.Timer;
import com.tosan.http.server.starter.util.AspectUtil;
import com.tosan.http.server.starter.util.TimerStatisticsUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.mockito.Mockito.*;

/**
 * @author AmirHossein ZamanZade
 * @since 10/26/2022
 */
public class TimerStatisticsAspectUTest {

    private TimerStatisticsAspect timerStatisticsAspect;
    private TimerStatisticsUtil timerStatisticsUtil;

    private AspectUtil aspectUtil;


    @BeforeEach
    public void setup() {
        timerStatisticsUtil = mock(TimerStatisticsUtil.class);
        aspectUtil = mock(AspectUtil.class);
        timerStatisticsAspect = new TimerStatisticsAspect(timerStatisticsUtil, aspectUtil);
    }

    @Test
    public void testCalculateStatistics_normalCall_callGenerateStatisticsCorrectly() throws Throwable {
        String serviceType = "TimerWebService";
        String serviceName = "TimerService";
        Timer timer = new Timer() {
            @Override
            public String serviceType() {
                return serviceType;
            }

            @Override
            public String serviceName() {
                return serviceName;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
        };
        when(aspectUtil.getAnnotation(any(), any())).thenReturn(timer);
        timerStatisticsAspect.calculateStatistics(mock(ProceedingJoinPoint.class));
        verify(timerStatisticsUtil).generateStatistics(eq(serviceType), eq(serviceName), anyLong(), anyLong());
    }
}
