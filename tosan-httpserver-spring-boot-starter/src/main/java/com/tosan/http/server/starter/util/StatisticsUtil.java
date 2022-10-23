package com.tosan.http.server.starter.util;

import com.tosan.http.server.starter.statistics.ServiceExecutionInfo;
import com.tosan.http.server.starter.statistics.Statistics;

/**
 * @author AmirHossein ZamanZade
 * @since 10/22/2022
 */
public class StatisticsUtil {

    public void generateStatistics(String serviceType, String serviceName, long startTime, long endTime) {
        ServiceExecutionInfo serviceExecutionInfo = new ServiceExecutionInfo(serviceType, serviceName,
                (endTime - startTime) / 1000);
        Statistics.getCurrentStatistics().getServiceExecutionsInfo().add(serviceExecutionInfo);
    }
}
