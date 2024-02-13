package com.tosan.http.server.starter.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author AmirHossein ZamanZade
 * @since 10/26/2022
 */
public class AspectUtil {

    @SuppressWarnings("unchecked, rawtypes")
    public <T extends Annotation> T getAnnotation(ProceedingJoinPoint pjp, Class clazz) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Method meth = AopUtils.getMostSpecificMethod(method, pjp.getTarget().getClass());
        return (T) meth.getAnnotation(clazz);
    }
}
