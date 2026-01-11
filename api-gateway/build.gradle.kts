group = "ru.yanin.practice"
version = "0.0.1-SNAPSHOT"
description = "api-gateway"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(project(":shared"))
    implementation("com.nimbusds:nimbus-jose-jwt:${property("nimbusVersion")}")
}
