package uk.co.kieranshaw.urlshortener;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Configuration
@EnableAspectJAutoProxy
@Aspect
public class AopConfiguration {
    /** Pointcut for execution of methods on {@link Service} annotation */
    @Pointcut("execution(public * (@org.springframework.stereotype.Service uk.co.kieranshaw..*).*(..))")
    public void serviceAnnotation() { }

    /** Pointcut for execution of methods on {@link Repository} annotation */
    @Pointcut("execution(public * (@org.springframework.stereotype.Repository uk.co.kieranshaw..*).*(..))")
    public void repositoryAnnotation() {}
    
    /** Pointcut for execution of methods on {@link Repository} annotation */
    @Pointcut("execution(public * (@org.springframework.stereotype.Controller uk.co.kieranshaw..*).*(..))")
    public void controllerAnnotation() {}

    @Pointcut("serviceAnnotation() || repositoryAnnotation() || controllerAnnotation()")
    public void performanceMonitor() {}

    @Bean
    public PerformanceMonitorInterceptor performanceMonitorInterceptor() {
        return new PerformanceMonitorInterceptor(true);
    }

    @Bean
    public Advisor performanceMonitorAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("uk.co.kieranshaw.urlshortener.AopConfiguration.performanceMonitor()");
        return new DefaultPointcutAdvisor(pointcut, performanceMonitorInterceptor());
    }
}