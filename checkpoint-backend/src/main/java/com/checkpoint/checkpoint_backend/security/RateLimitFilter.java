package com.checkpoint.checkpoint_backend.security;


import io.github.bucket4j.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean limited = path.startsWith("/api/auth/") || path.contains("/analyze-jd") || path.contains("/tailor");

        if (limited) {
            String key = request.getRemoteAddr() + ":" + path;
            Bucket bucket = buckets.computeIfAbsent(key, k -> newBucket());

            if (!bucket.tryConsume(1)) {
                response.setStatus(429);
                response.getWriter().write("Too many requests. Please slow down.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
