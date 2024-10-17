package com.mightyjava.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static org.apache.log4j.Logger.getLogger;

@Aspect
@Component
public class LoggerAspect {
    private static final Logger logger = getLogger(LoggerAspect.class);

    @Pointcut("execution(* com.mightyjava..*.*(..)) && !@annotation(org.springframework.scheduling.annotation.Scheduled)")
    private void generalPointcut() {
    }

    @AfterThrowing(pointcut = "generalPointcut()", throwing = "ex")
    public void exceptionLog(JoinPoint joinPoint, Exception ex) {
        logger.error(joinPoint.getTarget().getClass().getSimpleName() + " : " + joinPoint.getSignature().getName()
                     + " : " + ex.getMessage());
    }

    @Before("generalPointcut()")
    public void infoLog(JoinPoint joinPoint) {
        logger.info(joinPoint.getTarget().getClass().getSimpleName() + " : " + joinPoint.getSignature().getName());
    }
}
