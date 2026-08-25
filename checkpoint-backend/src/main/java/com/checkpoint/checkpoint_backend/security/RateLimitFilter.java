package com.checkpoint.checkpoint_backend.security;


import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.jedis.cas.JedisBasedProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import redis.clients.jedis.JedisPool;
import io.github.bucket4j.distributed.serialization.Mapper;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Supplier;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final JedisBasedProxyManager<String> proxyManager;

    public RateLimitFilter(@Value("${redis.url}") String redisUrl) {
        JedisPool jedisPool = new JedisPool(redisUrl);
        this.proxyManager = JedisBasedProxyManager.builderFor(jedisPool).withKeyMapper(Mapper.STRING).build();
    }

    private Supplier<BucketConfiguration> configSupplier() {
        return () -> io.github.bucket4j.BucketConfiguration.builder()
                .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean limited = path.startsWith("/api/auth/") || path.contains("/analyze-jd") || path.contains("/tailor");

        if (limited) {
            String userId = TenantContext.getUserId() != null ? TenantContext.getUserId().toString() : request.getRemoteAddr();
            String key = userId + ":" + path;

            Bucket bucket = proxyManager.builder().build(key, configSupplier());

            if (!bucket.tryConsume(1)) {
                response.setStatus(429);
                response.getWriter().write("Too many requests. Please slow down.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
