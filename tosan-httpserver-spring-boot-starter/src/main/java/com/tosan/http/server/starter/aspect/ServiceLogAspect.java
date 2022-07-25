package com.tosan.http.server.starter.aspect;

import com.tosan.http.server.starter.util.ServiceLogUtil;
import com.tosan.tools.logger.LogMode;
import com.tosan.tools.tostring.ToStringBuilderImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;

import java.util.Map;

/**
 * @author mina khoshnevisan
 * @since 7/25/2022
 */
@Order(1)
@Aspect
public class ServiceLogAspect {
    private ServiceLogUtil logUtil;

    public ServiceLogAspect(ServiceLogUtil logUtil) {
        this.logUtil = logUtil;
    }

    @Around(value = "execution(* (@org.springframework.web.bind.annotation.RequestMapping *).*(..))")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        ServiceLogUtil.LogDto logDto = logUtil.createLogDto(pjp);
        logRequest(logDto);
        Object object;
        try {
            object = pjp.proceed();
            logResponse(logDto, object);
            return object;
        } catch (Throwable ex) {
            logUtil.logException(logDto, ex);
            throw ex;
        } finally {
            ToStringBuilderImpl.cleanLogMode();
        }
    }

    protected void logRequest(ServiceLogUtil.LogDto logDto) {
        ProceedingJoinPoint pjp = logDto.getPjp();
        Logger logger = logDto.getLogger();
        String position = logDto.getPosition();
        Object[] methodParams = pjp.getArgs();

        ToStringBuilderImpl.setLogMode(LogMode.INFO);
        if (logger.isDebugEnabled()) {
            ToStringBuilderImpl.setLogMode(LogMode.DEBUG);
        }
        if (logger.isInfoEnabled()) {
            StringBuilder result = new StringBuilder(9);
            result.append(position);
            result.append(" Request: ( ");
            if (methodParams != null) {
                for (Object obj : methodParams) {
                    if (obj != null) {
                        if (obj instanceof Map) {
                            continue;
                        } else {
                            result.append("\"");
                            result.append(obj.getClass().getSimpleName());
                            result.append("\" : ");
                            result.append(obj);
                        }
                    } else {
                        result.append(' ');
                        result.append("null");
                    }
                    result.append(',');
                }
                result.deleteCharAt(result.length() - 1);
            }
            result.append(" )");
            logger.info(result.toString());
        }
    }

    private void logResponse(ServiceLogUtil.LogDto logDto, Object object) {
        Logger logger = logDto.getLogger();
        String position = logDto.getPosition();
        ToStringBuilderImpl.setLogMode(LogMode.INFO);
        if (logger.isDebugEnabled()) {
            ToStringBuilderImpl.setLogMode(LogMode.DEBUG);
        }
        if (logger.isInfoEnabled()) {
            StringBuilder result = new StringBuilder(4);
            result.append(position);
            result.append("Response: (");
            if (object != null) {
                result.append("\"");
                result.append(object.getClass().getSimpleName());
                result.append("\" : ");
                result.append("{ \n");
                result.append(object);
                result.append("} \n");
            } else {
                result.append("null");
            }
            result.append(")");
            logger.info(result.toString());
        }
    }
}