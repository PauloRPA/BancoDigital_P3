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

    private int repositoryDaoPadding = log.isDebugEnabled() ? 1 : 0;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    private void controllerMethods() {
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    private void serviceMethods() {
    }

    @Pointcut("execution(* com.prpa.bancodigital.*.*Repository.*(..))")
    private void repositoryMethods() {
    }

    @Pointcut("execution(* com.prpa.bancodigital.*.*.*Dao.findBy*(..)) || execution(* com.prpa.bancodigital.*.*.*Dao.save*(..))")
    private void daoMethods() {
    }

    @Pointcut("execution(* com.prpa.bancodigital.controller.advice.GlobalExceptionHandler.*(..))")
    private void globalExceptionHandlerExecution() {
    }

    @Around("daoMethods()")
    private Object infoLogDaoMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        log.info("{}[{}]: Query {} executada - {}ms",
                padding(),
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                System.currentTimeMillis() - start);
        return proceed;
    }

    @Around("repositoryMethods()")
    private Object infoLogRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("{}[{}]: executando {} ", padding(), joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName());
        repositoryDaoPadding++;
        Object proceed = joinPoint.proceed();
        repositoryDaoPadding--;
        return proceed;
    }

    private String padding() {
        return "\t".repeat(repositoryDaoPadding);
    }

    @Around("controllerMethods()")
    private Object debugLogControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logDebug(joinPoint);
    }

    @Around("serviceMethods()")
    private Object debugLogServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logDebug(joinPoint);
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

    private Object logDebug(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("[{}]: {} em ", joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName());
        Object proceed = joinPoint.proceed();
        log.debug("[{}]: {} Finalizado ", joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName());
        return proceed;
    }

}
