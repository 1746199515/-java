package com.atguigu.liteims.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LogAspect {

    // 定义切点：com.atguigu.liteims.controller 包下的所有方法
    @Pointcut("execution(* com.atguigu.liteims.controller..*.*(..))")
    public void controllerPointcut() {}

    // 环绕通知：在目标方法执行前后都执行
    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 1. 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.info("Request: {} {} - Args: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    Arrays.toString(joinPoint.getArgs()));
        }

        // 2. 执行目标方法
        Object result = joinPoint.proceed();

        // 3. 计算耗时
        long timeTaken = System.currentTimeMillis() - startTime;
        log.info("Response: {} ms", timeTaken);

        return result;
    }
}
