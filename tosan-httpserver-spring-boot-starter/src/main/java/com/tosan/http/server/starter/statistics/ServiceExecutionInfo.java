package com.tosan.http.server.starter.statistics;

/**
 * @author AmirHossein ZamanZade
 * @since 10/22/2022
 */
public record ServiceExecutionInfo(String serviceType, String serviceName, double duration) {
}
