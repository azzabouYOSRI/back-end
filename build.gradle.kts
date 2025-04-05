extra["springBootVersion"] = "3.4.3"
extra["springModulithVersion"] = "1.3.3"

plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.yosri.defensy"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

configurations.named("compileOnly") {
    extendsFrom(configurations.named("annotationProcessor").get())
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo1.maven.org/maven2/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.spring.io/milestone/") }
}

dependencies {
    // ✅ Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux") // WebClient (Reactive)
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // ✅ Spring Modulith
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-mongodb")

    // ✅ CSV & Compression
    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("org.apache.commons:commons-compress:1.26.0")

    // ✅ Environment Variables
    implementation("io.github.cdimascio:java-dotenv:5.2.2")
implementation("org.springframework.boot:spring-boot-starter-webflux")
implementation("io.projectreactor.netty:reactor-netty")
    // ✅ JSON & Kotlin Interop
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
implementation("io.netty:netty-handler")
    implementation("io.projectreactor.netty:reactor-netty")
implementation("org.springframework.boot:spring-boot-starter-webflux")
implementation("io.netty:netty-handler")
    // ✅ Utilities & Annotations
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("jakarta.annotation:jakarta.annotation-api")

    // ✅ Reactive Programming
    implementation("io.projectreactor:reactor-core")

    // ✅ Logging
    implementation("org.slf4j:slf4j-api")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // ✅ Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux") // WebClient (Reactive)

    // ✅ Reactor Netty for SslContextBuilder
    implementation("io.projectreactor.netty:reactor-netty-http") // 🧠 Enables SslContextBuilder

    // ✅ Standard Java SSL + Logging
    implementation("org.slf4j:slf4j-api")
    // ✅ Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // ✅ Development Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    // ✅ Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("uk.org.lidalia:slf4j-test:1.2.0")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:elasticsearch")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${property("springBootVersion")}")
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootRun {
    workingDir = rootProject.projectDir
}
tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from("src/main/resources") {
        include("**/*")
    }
}
