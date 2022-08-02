package com.tosan.http.server.starter.logger;


import com.tosan.http.server.starter.util.ToStringJsonUtil;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * @author Mostafa Abdollahi
 * @since 6/9/2021
 */
@Component
public class JsonServiceLogger extends ServiceLogger {

    @Override
    public String getRequestLog(String serviceName, Object[] methodArgs) {
        return createJson(serviceName, "request", methodArgs, null);
    }

    @Override
    public String getResponseLog(String serviceName, Object result, Double duration) {
        return createJson(serviceName, "response", new Object[]{result}, duration);
    }

    @Override
    public String getExceptionLog(String serviceName, Throwable ex, Double duration) {
        Map<String, Object> exception = new LinkedHashMap<>();
        exception.put("name", ex.getClass().getSimpleName());
        exception.put("message", ex.getMessage());
        exception.put("localizedMessage", ex.getLocalizedMessage());
        exception.put("stackTrace", getStackTrace(ex));
        return createJson(serviceName, "exception", new Object[]{exception}, duration);
    }

    private String getStackTrace(final Throwable throwable) {
        if (Objects.nonNull(throwable.getCause())) {
            return this.getStackTrace(throwable.getCause());
        }
        StringWriter stackTrace = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stackTrace, true);
        throwable.printStackTrace(printWriter);
        return stackTrace.getBuffer().toString();
    }

    private String createJson(String serviceName, String key, Object[] objects, Double duration) {
        Map<String, Object> object = new LinkedHashMap<>(3);
        object.put("service", serviceName);
        if (duration != null) {
            object.put("duration", duration + "s");
        }
        if (objects != null) {
            Map<String, Object> objectsMap = new LinkedHashMap<>(objects.length);
            for (Object obj : objects) {
                if (obj != null) {
                    objectsMap.put(obj.getClass().getSimpleName(), obj);
                }
            }
            object.put(key, objectsMap);
        }
        return ToStringJsonUtil.toJson(object);
    }
}