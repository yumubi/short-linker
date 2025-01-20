package io.hika.shortlinker

import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.pgclient.PgConnectOptions
import io.vertx.sqlclient.PoolOptions
import io.vertx.ext.web.Router

import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineRouterSupport
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.pgclient.PgBuilder
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.Tuple

class MainVerticle : CoroutineVerticle(), CoroutineRouterSupport {
    private lateinit var appConfig: Config
    private lateinit var sqlClient: SqlClient

    private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

    override suspend fun start() {
        appConfig = Config()

        val poolOptions = PoolOptions()
            .setMaxSize(5)

        val pgConnectOptions = PgConnectOptions()
            .setPort(appConfig.dbPort)
            .setHost(appConfig.dbHost)
            .setDatabase(appConfig.dbName)
            .setUser(appConfig.dbUser)
            .setPassword(appConfig.dbPassword)
           // .setSsl(true)





        try {
            sqlClient = PgBuilder
                .client()
                .with(poolOptions)
                .connectingTo(pgConnectOptions)
                .using(vertx)
                .build()
        } catch (e: Exception) {
            logger.error("Failed to connect to database", e)
        }


        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())

        setupRoutes(router)

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(appConfig.port, appConfig.host)
                .onFailure { e -> logger.error("Failed to start server", e) }
                .coAwait()

        logger.info("Server started at http://${appConfig.host}:${appConfig.port}")
    }

    private fun setupRoutes(router: Router) {

        router.post("/new").coHandler { ctx ->
            val form = ctx.request().formAttributes()
            val url = form.get("url")

            if (url == null) {
                ctx.response()
                    .setStatusCode(400)
                    .end("url is required")
                return@coHandler
            }

            logger.info("url: $url")

            val seqResult = try {
                sqlClient.preparedQuery("SELECT nextval('short_urls_id_seq')")
                    .execute()
                    .coAwait()
            } catch (e: Exception) {
                logger.error("Failed to get next sequence value", e)
                ctx.response()
                    .setStatusCode(500)
                    .end("Failed to generate short URL")
                return@coHandler
            }


            val id = seqResult.first().getLong(0)
            val shortPath = UrlShortener.encode(id)

            val result = try {
                sqlClient.preparedQuery(
                    "INSERT INTO short_urls(id, original_url, short_path) VALUES($1, $2, $3) RETURNING id"
                )
                    .execute(Tuple.of(id, url, shortPath))
                    .onFailure { e -> logger.error("Failed to insert url", e) }
                    .coAwait()


        } catch (e: Exception) {
                logger.error("Failed to insert url", e)
                ctx.response()
                    .setStatusCode(500)
                    .end("Failed to insert url")
                return@coHandler
        }
            ctx.response()
                .putHeader("content-type", "application/json")
                .end(json {
                    obj("url" to "${appConfig.website}$shortPath")
                }.encode())
        }


        router.get("/*").coHandler { ctx ->
            val path = ctx.request().path().substring(2)
            logger.info("Path: $path")
           val result = sqlClient.preparedQuery(
                "SELECT original_url FROM short_urls WHERE short_path = $1"
            )
                .execute(Tuple.of(path))
                .coAwait()


            if (result.size() == 0) {
                ctx.response().setStatusCode(404).end()
                return@coHandler
            }


            val originalUrl = result.first().getString("original_url")
            logger.info("Redirecting to $originalUrl")
            ctx.response()
                .setStatusCode(301)
                .putHeader("Location", originalUrl)
                .end()
        }
    }
}

