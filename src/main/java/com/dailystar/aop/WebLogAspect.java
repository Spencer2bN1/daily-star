package com.dailystar.aop;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class WebLogAspect {

    @Around("execution(* com.dailystar.controller..*(..))")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String method = joinPoint.getSignature().toShortString();
        log.info("request start: {} args={}", method, Arrays.toString(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            log.info("request end: {} cost={}ms", method, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable ex) {
            log.error("request error: {} cost={}ms", method, System.currentTimeMillis() - start, ex);
            throw ex;
        }
    }
}
