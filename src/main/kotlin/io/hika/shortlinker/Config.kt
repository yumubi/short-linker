package io.hika.shortlinker

import io.vertx.core.json.JsonObject

data class Config(
    val host: String = System.getenv("SURL_HOST") ?: "127.0.0.1",
    val port: Int = System.getenv("SURL_PORT")?.toInt() ?: 8080,
    val website: String = System.getenv("SURL_WEBSITE") ?: "http://localhost:8080/s",
    val dbHost: String = System.getenv("DB_HOST") ?: "localhost",
    val dbPort: Int = System.getenv("DB_PORT")?.toInt() ?: 5432,
    val dbName: String = System.getenv("DB_NAME") ?: "urlshortener",
    val dbUser: String = System.getenv("DB_USER") ?: "postgres",
    val dbPassword: String = System.getenv("DB_PASSWORD") ?: "postgres",
    val redisUrl: String = System.getenv("REDIS_URL") ?: "redis://host:6379/4",
    val redisPassword: String = System.getenv("REDIS_PASSWORD") ?: "password",
    val rateLimit: RateLimitConfig = RateLimitConfig(),
) {
    fun toJson() = JsonObject().apply {
        put("host", host)
        put("port", port)
        put("website", website)
        put("db_host", dbHost)
        put("db_port", dbPort)
        put("db_name", dbName)
        put("db_user", dbUser)
        put("db_password", dbPassword)
    }
}

data class RateLimitConfig(
    val enabled: Boolean = true,
    val requestsPerMinute: Int = 10,
    val timeoutSeconds: Int = 1
)
