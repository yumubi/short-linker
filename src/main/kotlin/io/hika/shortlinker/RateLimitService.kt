package io.hika.shortlinker

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import java.time.Duration

class RateLimitService {
    private val rateLimiters = mutableMapOf<String, RateLimiter>()

    fun getRateLimiter(clientId: String): RateLimiter {
        return rateLimiters.getOrPut(clientId) {
            RateLimiter.of(clientId, RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(1))
                .build())
        }
    }
}
