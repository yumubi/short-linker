package io.hika.shortlinker
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.redis.client.Redis
import io.vertx.redis.client.RedisAPI
import io.vertx.redis.client.RedisOptions
import org.slf4j.LoggerFactory

class RedisCacheService(vertx: Vertx, appConfig: Config) {
    private val logger = LoggerFactory.getLogger(RedisCacheService::class.java)
    private val redis: RedisAPI
    private val cacheExpiration = 3600L //1h

    init {
        val redisClient = Redis.createClient(
            vertx,
            RedisOptions()
                .setConnectionString(appConfig.redisUrl)
                .setPassword(appConfig.redisPassword)
                .setMaxPoolSize(8)
                .setMaxPoolWaiting(32)
        )
        redis = RedisAPI.api(redisClient)
    }

    suspend fun get(key: String): String? {
        return try {
            redis.get(key).coAwait()?.toString()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun set(key: String, value: String) {
        try {
            redis.setex(key, cacheExpiration.toString(), value).coAwait()
        } catch (e: Exception) {
          logger.error("Failed to set cache", e)
        }
    }
}
