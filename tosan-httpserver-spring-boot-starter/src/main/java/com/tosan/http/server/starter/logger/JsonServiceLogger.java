package com.tosan.http.server.starter.logger;


import com.tosan.http.server.starter.config.ServiceLoggingConfig;
import com.tosan.http.server.starter.util.ToStringJsonUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mostafa Abdollahi
 * @since 6/9/2021
 */
public class JsonServiceLogger extends ServiceLogger {

    private final ServiceLoggingConfig serviceLoggingConfig;
    private final ToStringJsonUtil toStringJsonUtil;

    public JsonServiceLogger(ServiceLoggingConfig serviceLoggingConfig, ToStringJsonUtil toStringJsonUtil) {
        this.serviceLoggingConfig = serviceLoggingConfig;
        this.toStringJsonUtil = toStringJsonUtil;
    }

    @Override
    public String getRequestLog(String serviceName, Object[] methodArgs, String[] parameterNames) {
        return createJson(serviceName, "request", methodArgs, parameterNames, null);
    }

    @Override
    public String getResponseLog(String serviceName, Object result, Double duration) {
        return createJson(serviceName, "response", new Object[]{result}, null, duration);
    }

    @Override
    public String getExceptionLog(String serviceName, Throwable ex, Double duration) {
        Map<String, Object> exception = new LinkedHashMap<>();
        exception.put("name", ex.getClass().getSimpleName());
        exception.put("message", ex.getMessage());
        exception.put("localizedMessage", ex.getLocalizedMessage());
        exception.put("stackTrace", getStackTrace(ex));
        return createJson(serviceName, "exception", new Object[]{exception}, null, duration);
    }

    private List<String> getStackTrace(final Throwable throwable) {
        List<String> stackTrace = new ArrayList<>();
        for (StackTraceElement element : throwable.getStackTrace()) {
            stackTrace.add(element.toString());
            if (stackTrace.size() > 14) {
                break;
            }
        }
        return stackTrace;
    }

    private String createJson(String serviceName, String key, Object[] objects, Object[] parameterNames, Double duration) {
        Map<String, Object> object = new LinkedHashMap<>(3);
        object.put("service", serviceName);
        if (duration != null) {
            object.put("duration", duration + "s");
        }
        if (objects != null) {
            Map<String, Object> objectsMap = new LinkedHashMap<>(objects.length);
            for (int i = 0; i < objects.length; i++) {
                Object obj = objects[i];
                if (obj != null && !ignoreArgument(obj)) {
                    if (parameterNames != null && parameterNames.length > i && parameterNames[i] != null) {
                        objectsMap.put((String) parameterNames[i], obj);
                    } else {
                        objectsMap.put(obj.getClass().getSimpleName(), obj);
                    }
                }
            }
            object.put(key, objectsMap);
        }
        return toStringJsonUtil.toJson(object);
    }

    public boolean ignoreArgument(Object object) {
        List<Class<?>> classes = serviceLoggingConfig.getIgnoredParameterTypes();
        for (Class<?> clazz : classes) {
            if (clazz.isInstance(object)) {
                return true;
            }
        }
        return false;
    }
}