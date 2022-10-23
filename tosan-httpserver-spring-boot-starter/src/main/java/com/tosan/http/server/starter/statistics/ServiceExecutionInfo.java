package com.tosan.http.server.starter.statistics;

/**
 * @author AmirHossein ZamanZade
 * @since 10/22/2022
 */
public class ServiceExecutionInfo {
    private final String serviceType;
    private final String serviceName;
    private final long duration;

    public ServiceExecutionInfo(String serviceType, String serviceName, long duration) {
        this.serviceType = serviceType;
        this.serviceName = serviceName;
        this.duration = duration;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public long getDuration() {
        return duration;
    }
}
