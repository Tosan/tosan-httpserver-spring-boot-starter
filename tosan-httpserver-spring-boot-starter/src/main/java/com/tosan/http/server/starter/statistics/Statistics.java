package com.tosan.http.server.starter.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AmirHossein ZamanZade
 * @since 10/22/2022
 */
public class Statistics {
    private static final ThreadLocal<Statistics> APPLICATION_STATISTICS = new ThreadLocal<>();
    private final List<ServiceExecutionInfo> serviceExecutionsInfo;

    private Statistics() {
        serviceExecutionsInfo = new ArrayList<>();
    }

    public static Statistics getApplicationStatistics() {
        if (APPLICATION_STATISTICS.get() == null) {
            APPLICATION_STATISTICS.set(new Statistics());
        }
        return APPLICATION_STATISTICS.get();
    }

    public static void cleanupSession() {
        APPLICATION_STATISTICS.remove();
    }

    public List<ServiceExecutionInfo> getServiceExecutionsInfo() {
        return serviceExecutionsInfo;
    }
}
