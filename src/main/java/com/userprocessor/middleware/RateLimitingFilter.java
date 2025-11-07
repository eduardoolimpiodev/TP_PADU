package com.userprocessor.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userprocessor.dto.ApiResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final long TIME_WINDOW_MS = 60 * 1000;

    private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = getClientIpAddress(httpRequest);
        String key = clientIp + ":" + httpRequest.getRequestURI();

        if (isRateLimited(key)) {
            sendRateLimitResponse(httpResponse);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isRateLimited(String key) {
        long currentTime = System.currentTimeMillis();
        
        RequestCounter counter = requestCounts.computeIfAbsent(key, k -> new RequestCounter());
        
        synchronized (counter) {
            if (currentTime - counter.windowStart > TIME_WINDOW_MS) {
                counter.windowStart = currentTime;
                counter.requestCount.set(1);
                return false;
            }
            
            return counter.requestCount.incrementAndGet() > MAX_REQUESTS_PER_MINUTE;
        }
    }

    private void sendRateLimitResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
        response.setContentType("application/json");
        
        ApiResponse<Object> apiResponse = ApiResponse.error(
            "Rate limit exceeded", 
            "Too many requests. Maximum " + MAX_REQUESTS_PER_MINUTE + " requests per minute allowed."
        );
        
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    private static class RequestCounter {
        volatile long windowStart = System.currentTimeMillis();
        final AtomicInteger requestCount = new AtomicInteger(0);
    }
}
