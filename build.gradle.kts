import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("org.springframework.boot") version "3.2.7"
    id("io.spring.dependency-management") version "1.1.5"

    // https://github.com/n0mer/gradle-git-properties/releases
    id("com.gorylenko.gradle-git-properties") version "2.4.1"

    // https://github.com/spotbugs/spotbugs-gradle-plugin/releases
    id("com.github.spotbugs") version "6.0.7"

    // https://github.com/researchgate/gradle-release
    // https://mvnrepository.com/artifact/net.researchgate.release/net.researchgate.release.gradle.plugin
    id("net.researchgate.release") version "3.0.2"

    // https://github.com/ben-manes/gradle-versions-plugin/releases
    id("com.github.ben-manes.versions") version "0.51.0"

    java
    idea
    application
    kotlin("jvm")
}

group = "com.msd"
version = "1.0-SNAPSHOT"

configurations {
    all {
        exclude("org.springframework.boot", "spring-boot-starter-logging")

        // Can"t exclude because of this: https://github.com/testcontainers/testcontainers-java/issues/970
        // exclude("junit", "junit")
    }
}
configurations.named("spotbugs").configure {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.ow2.asm") {
            useVersion("9.5")
            because("Asm 9.5 is required for JDK 21 support")
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
}

extra["springCloudVersion"] = "2023.0.3"
extra["springShellVersion"] = "3.3.2"

dependencyManagement {
    imports {
        // https://github.com/spring-projects/spring-boot/releases
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.3")

        // To avoid specifying the version of each dependency, use a BOM or Bill Of Materials.
        // https://github.com/testcontainers/testcontainers-java/releases
        mavenBom("org.testcontainers:testcontainers-bom:1.18.3")

        mavenBom("org.springframework.shell:spring-shell-dependencies:${property("springShellVersion")}")


        //https://immutables.github.io/
        mavenBom("org.immutables:bom:2.9.2")

        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${
            property(
                    "springCloudVersion")
        }")
    }

    dependencies {
        // https://github.com/apache/logging-log4j2/tags
        dependencySet("org.apache.logging.log4j:2.20.0") {
            entry("log4j-core")
            entry("log4j-api")
            entry("log4j-web")
        }
    }
}


dependencies {
    // https://github.com/spotbugs/spotbugs/tags
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.8.3")
    testCompileOnly("com.github.spotbugs:spotbugs-annotations:4.8.3")

    // https://github.com/KengoTODA/findbugs-slf4j
    spotbugsPlugins("jp.skypencil.findbugs.slf4j:bug-pattern:1.5.0@jar")

    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:slf4j-simple:1.7.36")

    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.12.0")

    annotationProcessor("org.immutables:value")
    compileOnly("org.immutables:builder")
    compileOnly("org.immutables:value-annotations")


    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.shell:spring-shell-starter")
    testImplementation("org.springframework.shell:spring-shell-starter-test")



    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")


}

spotbugs {
    toolVersion.set("4.7.3")
    excludeFilter.set(file("${project.rootDir}/findbugs-exclude.xml"))
}

tasks {
    spotbugsMain {
        effort.set(com.github.spotbugs.snom.Effort.MAX)
        reports.create("html") {
            enabled = true
        }
    }

    spotbugsTest {
        ignoreFailures = true
        reportLevel.set(com.github.spotbugs.snom.Confidence.HIGH)
        effort.set(com.github.spotbugs.snom.Effort.MIN)
        reports.create("html") {
            enabled = true
        }
    }
}

tasks.compileJava {
    dependsOn("processResources")
    options.release.set(21)
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:deprecation"))
}

tasks.processResources {
    val tokens = mapOf(
            "application.version" to project.version,
            "application.description" to project.description
    )
    filesMatching("**/*.yml") {
        filter<ReplaceTokens>("tokens" to tokens)
    }
}

tasks.test {
    failFast = false
    enableAssertions = true

    // Enable JUnit 5 (Gradle 4.6+).
    useJUnitPlatform()

    testLogging {
        events("PASSED", "STARTED", "FAILED", "SKIPPED")
        // Set to true if you want to see output from tests
        showStandardStreams = false
        setExceptionFormat("FULL")
    }

    systemProperty("io.netty.leakDetectionLevel", "paranoid")
}

defaultTasks("spotlessApply", "build")


kotlin {
    jvmToolchain(21)
}


application {
    mainClass = "com.msd.file_compressor.MainApplication"
}
