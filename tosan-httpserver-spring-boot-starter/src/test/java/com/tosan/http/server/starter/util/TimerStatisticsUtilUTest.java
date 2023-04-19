package com.tosan.http.server.starter.util;

import com.tosan.http.server.starter.statistics.ServiceExecutionInfo;
import com.tosan.http.server.starter.statistics.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author AmirHossein ZamanZade
 * @since 10/23/2022
 */
public class TimerStatisticsUtilUTest {

    private TimerStatisticsUtil timerStatisticsUtil;

    @BeforeEach
    public void setup() {
        timerStatisticsUtil = new TimerStatisticsUtil();
    }

    @Test
    public void testGenerateStatistics_normalCall_generateCorrectStatistics() {
        String serviceType = "httpserver";
        String serviceName = "statistics";
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 1000;
        timerStatisticsUtil.generateStatistics(serviceType, serviceName, startTime, endTime);
        List<ServiceExecutionInfo> serviceExecutionInfos = Statistics.getApplicationStatistics().getServiceExecutionsInfo();
        assertNotNull(serviceExecutionInfos);
        ServiceExecutionInfo serviceExecutionInfo = serviceExecutionInfos.get(0);
        assertNotNull(serviceExecutionInfo);
        assertEquals(serviceExecutionInfo.serviceType(), serviceType);
        assertEquals(serviceExecutionInfo.serviceName(), serviceName);
        assertEquals(serviceExecutionInfo.duration(), (endTime - startTime) / 1000);
    }
}
