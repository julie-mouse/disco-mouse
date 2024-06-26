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

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "BotApplication"
    }

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}