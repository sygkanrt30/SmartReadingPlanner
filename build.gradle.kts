import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

plugins {
    id("java")
    id("fr.brouillard.oss.gradle.jgitver")
    id("io.spring.dependency-management")
    id("org.springframework.boot")
    id("name.remal.sonarlint") apply false
    id("com.diffplug.spotless") apply false
    id("io.freefair.lombok") version "8.14.1"
}

group = "ru.yanin.practice"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "org.springframework.boot")

    tasks.test {
        useJUnitPlatform()
        systemProperty("junit.jupiter.execution.parallel.enabled", "true")
        systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
    }


    dependencyManagement {
        imports {
            mavenBom(BOM_COORDINATES)
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        }
    }

    dependencies {
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.instancio:instancio-junit:${property("instancioVersion")}")

        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.mapstruct:mapstruct:${property("mapstructVersion")}")
        annotationProcessor("org.mapstruct:mapstruct-processor:${property("mapstructVersion")}")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test> {
        systemProperty("file.encoding", "UTF-8")
    }

    tasks.register("prepareKotlinBuildScriptModel") {

    }
}
