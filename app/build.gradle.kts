import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("application")
    id("checkstyle")
    id("jacoco")

}

//application {
//    mainClass.set("hexlet.code.App")
//}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("gg.jte:jte:3.0.1")
    implementation("io.javalin:javalin-rendering:5.6.2")
    implementation("io.javalin:javalin:5.6.2")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("gg.jte:jte:3.0.1")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    testImplementation("org.jacoco:org.jacoco.core:0.8.10")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        showStandardStreams = true
    }
}