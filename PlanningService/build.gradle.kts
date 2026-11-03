group = "ru.yanin.practice"
version = "0.0.1-Planning-Service"
description = "PlanningService"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation(project(":shared"))
    runtimeOnly("org.postgresql:postgresql")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:${property("resilience4jVersion")}") {
        exclude(group = "io.github.resilience4j", module = "resilience4j-rxjava3")
        exclude(group = "io.github.resilience4j", module = "resilience4j-rxjava2")
        exclude(group = "io.reactivex.rxjava3", module = "rxjava")
    }

    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

