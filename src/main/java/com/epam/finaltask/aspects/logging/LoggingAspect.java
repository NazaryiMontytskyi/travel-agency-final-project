package com.epam.finaltask.aspects.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.epam.finaltask.auth.AuthenticationService..*(..))")
    public void serviceMethods(){

    }

    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Invocation of method: {} with arguments {}",
                joinPoint.getSignature(),
                joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result){
        log.info("Method {} ended successfully. Result {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint point, Exception ex){
        log.error("Method {} thrown an exception: {}", point.getSignature(), ex.getStackTrace());
    }
}
