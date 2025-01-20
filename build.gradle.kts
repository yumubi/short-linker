plugins {
    kotlin("jvm") version "1.9.21"
    application
}

group = "io.hika"
version = "1.0-SNAPSHOT"
val vertxVersion = "4.5.11"
repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-lang-kotlin")
    implementation("io.vertx:vertx-lang-kotlin-coroutines")
    implementation("io.vertx:vertx-pg-client")
    implementation("io.vertx:vertx-redis-client")
    implementation("io.github.resilience4j:resilience4j-ratelimiter:2.1.0")
    implementation("io.github.resilience4j:resilience4j-kotlin:2.1.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.+")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    testImplementation("io.vertx:vertx-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

application {
    mainClass.set("io.goji.shortlinker.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
