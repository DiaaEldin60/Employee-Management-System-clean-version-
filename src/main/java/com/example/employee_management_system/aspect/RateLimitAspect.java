package com.example.employee_management_system.aspect;

import com.example.employee_management_system.annotation.RateLimited;
import com.example.employee_management_system.exception.RateLimitExceededException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
/// by me
/// creating aspect for rate limiting
/// @Aspect - marks the class as an aspect,
/// Aspect is a class that contains one or more pointcuts and advice
/// Pointcut is a method that matches a set of join points
/// Advice is a method that is executed at a specific point in the application
/// @Around - marks the method as an advice,
/// @Component - marks the class as a Spring component
@Aspect
@Component
public class RateLimitAspect {

    private final ConcurrentHashMap<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();

    @Around("@annotation(com.example.employee_management_system.annotation.RateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String clientId = getClientId(request);
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RateLimited rateLimited = signature.getMethod().getAnnotation(RateLimited.class);
        
        int limit = rateLimited.limit();
        int period = rateLimited.period();
        
        RateLimitBucket bucket = buckets.computeIfAbsent(clientId, k -> new RateLimitBucket(limit, period));
        
        if (!bucket.tryConsume()) {
            throw new RateLimitExceededException("Rate limit exceeded. Please try again later.", period);
        }
        
        return joinPoint.proceed();
    }

    private String getClientId(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        return ip + ":" + (userAgent != null ? userAgent : "");
    }
    /// by me
    /// class to store rate limit information
    /// static class to avoid creating new instances for each request
    /// No dependency on outer class
    /// Memory efficiency - By making it static, it doesn't create an implicit reference to the outer

    private static class RateLimitBucket {
        private final int limit;
        private final int windowSeconds;
        private final AtomicInteger count;
        private Instant windowStart;

        public RateLimitBucket(int limit, int windowSeconds) {
            this.limit = limit;
            this.windowSeconds = windowSeconds;
            this.count = new AtomicInteger(0);
            this.windowStart = Instant.now();
        }
        /// by me
        /// method to check if the request is within the rate limit
        /// synchronized to ensure thread safety
        public synchronized boolean tryConsume() {
            Instant now = Instant.now();
            long secondsSinceStart = ChronoUnit.SECONDS.between(windowStart, now);
            
            if (secondsSinceStart >= windowSeconds) {
                // Reset window
                count.set(0);
                windowStart = now;
            }
            
            return count.incrementAndGet() <= limit;
        }
    }
}
