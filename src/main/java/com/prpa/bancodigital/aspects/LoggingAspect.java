package com.prpa.bancodigital.aspects;

import com.prpa.bancodigital.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "application.aop", name = "logging", havingValue = "true")
public class LoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    private void controllerMethods() {
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    private void serviceMethods() {
    }

    @Pointcut("execution(* com.prpa.bancodigital.controller.advice.GlobalExceptionHandler.*(..))")
    private void globalExceptionHandlerExecution() {
    }

    @Around("controllerMethods()")
    private Object debugLogControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Iniciando: {} em [{}]", joinPoint.getSignature().getName(), joinPoint.getTarget().getClass().getSimpleName());
        Object proceed = joinPoint.proceed();
        log.debug("Finalizando: {} em [{}]", joinPoint.getSignature().getName(), joinPoint.getTarget().getClass().getSimpleName());
        return proceed;
    }

    @Around("serviceMethods()")
    private Object debugLogServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Executando: {} em [{}]", joinPoint.getSignature().getName(), joinPoint.getTarget().getClass().getSimpleName());
        Object proceed = joinPoint.proceed();
        log.debug("Finalizando execução de: {} em [{}]", joinPoint.getSignature().getName(), joinPoint.getTarget().getClass().getSimpleName());
        return proceed;
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "exception")
    private void debugLogExceptionMethods(JoinPoint joinPoint, Throwable exception) {
        if (exception instanceof ApiException apiException)
            log.debug("Exceção lançada com status {}. Motivo: {}", apiException.getStatusCode(), apiException.getReason());
        else
            log.debug("Exceção lançada! {}", exception.toString());
    }

    @Before(value = "globalExceptionHandlerExecution()")
    private void debugLogGlobalExceptionHandler(JoinPoint joinPoint) {
        log.debug("Exceção capturada por {}", joinPoint.getSignature().getName());
    }

}
