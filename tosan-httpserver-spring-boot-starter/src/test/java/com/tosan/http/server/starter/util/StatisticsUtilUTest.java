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
public class StatisticsUtilUTest {

    private StatisticsUtil statisticsUtil;

    @BeforeEach
    public void setup() {
        statisticsUtil = new StatisticsUtil();
    }

    @Test
    public void testGenerateStatistics_normalCall_generateCorrectStatistics() {
        String serviceType = "httpserver";
        String serviceName = "statistics";
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 1000;
        statisticsUtil.generateStatistics(serviceType, serviceName, startTime, endTime);
        List<ServiceExecutionInfo> serviceExecutionInfos = Statistics.getCurrentStatistics().getServiceExecutionsInfo();
        assertNotNull(serviceExecutionInfos);
        ServiceExecutionInfo serviceExecutionInfo = serviceExecutionInfos.get(0);
        assertNotNull(serviceExecutionInfo);
        assertEquals(serviceExecutionInfo.getServiceType(), serviceType);
        assertEquals(serviceExecutionInfo.getServiceName(), serviceName);
        assertEquals(serviceExecutionInfo.getDuration(), (endTime - startTime) / 1000);
    }
}
