extra["springBootVersion"] = "3.4.3"
plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.5"
}

group = "com.yosri.defensy"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo1.maven.org/maven2/") } // Ensures Maven Central is used
    maven { url = uri("https://jitpack.io") }  // Jitpack if needed for some dependencies
    maven { url = uri("https://repo.spring.io/milestone/") } // Spring milestone repo
}

extra["springModulithVersion"] = "1.3.3"

dependencies {
    // ✅ Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")  // ✅ MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")  // ✅ Elasticsearch
    implementation("org.springframework.boot:spring-boot-starter-actuator")
 // ✅ csv Dependencies
    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("org.apache.commons:commons-compress:1.22") // Updated for security

     // ✅ Logging (Structured JSON Logging)
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

//    // Spring Security
//    implementation("org.springframework.boot:spring-boot-starter-security")
//
//    // Keycloak Spring Boot Adapter
//    implementation("org.keycloak:keycloak-spring-boot-starter:22.0.5")

    // ✅ Spring Modulith Dependencies
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-mongodb")
    implementation("org.mockito:mockito-inline:5.2.0")

    // ✅ Lombok (Avoid duplication)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")


    // ✅ Development Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    // ✅ Testing Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.assertj:assertj-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")


    // ✅ JUnit 5 for Unit Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core:5.5.0")

    // ✅ Mockito for Mocking
    testImplementation("org.mockito:mockito-junit-jupiter")



    // ✅ SLF4J Test for Logging Verification
    testImplementation("uk.org.lidalia:slf4j-test:1.2.0")

    // ✅ Testcontainers for Elasticsearch (Elasticsearch Integration Tests)
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:elasticsearch")

    // ✅ Jackson for JSON Parsing in Tests
    implementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.google.guava:guava:33.0.0-jre") // Secure version
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21") // Updated for security
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
