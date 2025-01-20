package io.hika.shortlinker

import io.vertx.ext.web.Router
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import java.util.concurrent.atomic.AtomicLong

class MetricsService {
    private val cacheHits = AtomicLong(0)
    private val cacheMisses = AtomicLong(0)
    private val rateLimitHits = AtomicLong(0)
    private val totalRequests = AtomicLong(0)

    fun recordCacheHit() = cacheHits.incrementAndGet()
    fun recordCacheMiss() = cacheMisses.incrementAndGet()
    fun recordRateLimitHit() = rateLimitHits.incrementAndGet()
    fun recordRequest() = totalRequests.incrementAndGet()

    fun setupMetricsEndpoint(router: Router) {
        router.get("/metrics").handler { ctx ->
            ctx.response()
                .putHeader("content-type", "application/json")
                .end(json {
                    obj(
                        "cache_hits" to cacheHits.get(),
                        "cache_misses" to cacheMisses.get(),
                        "rate_limit_hits" to rateLimitHits.get(),
                        "total_requests" to totalRequests.get(),
                        "cache_hit_ratio" to (cacheHits.get().toDouble() /
                            (cacheHits.get() + cacheMisses.get()))
                    )
                }.encode())
        }
    }
}
