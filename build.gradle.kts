plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.serialization") version "1.9.25"
    application
}

val ktorVersion = "2.3.12"
val kotlinVersion = "1.9.25"
val exposedVersion = "0.54.0"
val koinVersion = "3.5.6"
val logbackVersion = "1.5.20"
val postgresqlVersion = "42.7.7"
val jbcryptVersion = "0.4"

group = "com.mathstack"
version = "0.1.0"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("org.mindrot:jbcrypt:$jbcryptVersion")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
}
