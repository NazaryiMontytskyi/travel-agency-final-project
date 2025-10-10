package com.epam.finaltask.aspects.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerLayer() {
    }


    @Pointcut("within(com.epam.finaltask.service..*)")
    public void serviceLayer() {
    }

    @Pointcut("(within(com.epam.finaltask.auth..*) || within(com.epam.finaltask.token..*)) && !within(com.epam.finaltask.token.JwtAuthenticationFilter)")
    public void securityLayer() {
    }

    @Pointcut("controllerLayer() || serviceLayer() || securityLayer()")
    public void applicationLayers() {
    }

    @Before("applicationLayers()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("==> Enter: {}.{}() with argument[s] = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "applicationLayers()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("<== Exit: {}.{}() with result = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result);
    }

    @AfterThrowing(pointcut = "applicationLayers()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("!!! Exception in {}.{}() with cause = '{}' and exception = '{}'",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                ex.getCause() != null ? ex.getCause() : "NULL",
                ex.getMessage());
    }

    @Around("applicationLayers()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("==> Around Enter: {}.{}() with argument[s] = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed(); // Виконання самого методу

        stopWatch.stop();

        log.info("<== Around Exit: {}.{}() with result = {}; Execution time = {} ms",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result,
                stopWatch.getTotalTimeMillis());

        return result;
    }
}