package com.tosan.http.server.starter.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AmirHossein ZamanZade
 * @since 10/22/2022
 */
public class Statistics {
    private static final ThreadLocal<Statistics> currentStatistics = new ThreadLocal<>();
    private final List<ServiceExecutionInfo> serviceExecutionsInfo;

    private Statistics() {
        serviceExecutionsInfo = new ArrayList<>();
    }

    public static Statistics getCurrentStatistics() {
        if (currentStatistics.get() == null) {
            currentStatistics.set(new Statistics());
        }
        return currentStatistics.get();
    }

    public static void cleanupSession() {
        currentStatistics.remove();
    }

    public List<ServiceExecutionInfo> getServiceExecutionsInfo() {
        return serviceExecutionsInfo;
    }
}
