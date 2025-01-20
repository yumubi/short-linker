package io.hika.shortlinker

import io.vertx.core.Vertx


fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle())
}
