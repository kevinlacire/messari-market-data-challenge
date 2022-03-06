plugins {
    application
    `maven-publish`
    id("io.freefair.lombok") version "6.1.0"
    id("org.springframework.boot") version "2.5.3"
}

group = "dev.ultimaratio.messari"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.bootJar {
    archiveFileName.set(rootProject.name + ".jar")
}

val projectGroup = project.group.toString()
val projectVersion = project.version.toString()

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = projectGroup
            artifactId = project.name
            version = projectVersion

            artifact(tasks.bootJar)
        }
    }
}

val junitVersion = "5.8.2"
val camelVersion = "3.15.0"
val lombokVersion = "1.18.20"
val springBootVersion = "2.5.1"

dependencies {
    // Commons
    implementation("com.opencsv:opencsv:5.5.2")
    implementation("me.paulschwarz:spring-dotenv:2.4.1")

    // Camel
    implementation("org.apache.camel:camel-core:${camelVersion}")
    implementation("org.apache.camel:camel-jackson:${camelVersion}")
    implementation("org.apache.camel:camel-stream:${camelVersion}")

    // Spring
    implementation("org.apache.camel.springboot:camel-spring-boot-starter:${camelVersion}")
    implementation("org.apache.camel.springboot:camel-bean-starter:${camelVersion}")
    implementation("org.springframework.boot:spring-boot-starter-web:${springBootVersion}")

    // Annotation Processor
    implementation("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    // Test
    testImplementation("org.projectlombok:lombok:${lombokVersion}")
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
}

application {
    mainClass.set("dev.ultimaratio.messari.App")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}