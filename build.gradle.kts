plugins {
    kotlin("jvm") version "1.9.0"
}

group = "org.jmouse"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("dev.kord:kord-core:0.11.1")
    implementation("ch.qos.logback:logback-classic:1.3.11")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}