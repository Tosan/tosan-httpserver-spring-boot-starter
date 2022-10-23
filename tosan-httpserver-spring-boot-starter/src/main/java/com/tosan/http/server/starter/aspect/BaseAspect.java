package com.tosan.http.server.starter.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author AmirHossein ZamanZade
 * @since 10/23/2022
 */
public abstract class BaseAspect {
    protected <T extends Annotation> T getAnnotation(ProceedingJoinPoint pjp, Class clazz) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Method meth = AopUtils.getMostSpecificMethod(method, pjp.getTarget().getClass());
        return (T) meth.getAnnotation(clazz);
    }
}
