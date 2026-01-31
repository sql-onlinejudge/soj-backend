plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    kotlin("kapt") version "1.9.22"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "10.6.0"
}

group = "me.suhyun"
version = "0.0.1-SNAPSHOT"
description = "soj"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // MySQL
    runtimeOnly("com.mysql:mysql-connector-j:8.3.0")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Elasticsearch
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

    // MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.47.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.47.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.47.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.47.0")
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.47.0")

    // Flyway
    implementation("org.flywaydb:flyway-core:10.6.0")
    implementation("org.flywaydb:flyway-mysql:10.6.0")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // Sentry
    implementation("io.sentry:sentry-spring-boot-starter-jakarta:7.3.0")
    implementation("io.sentry:sentry-logback:7.3.0")

    // Docker Java (컨테이너 제어)
    implementation("com.github.docker-java:docker-java-core:3.3.4")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.3.4")

    // API 문서화
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")

    // 로그
    implementation("net.logstash.logback:logstash-logback-encoder:9.0")

    // SQL Parser
    implementation("com.github.jsqlparser:jsqlparser:5.0")

    // WebClient
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
