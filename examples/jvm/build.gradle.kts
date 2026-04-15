plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Fursuit.TV SDK
    implementation("com.furrist.rp:fursuit-tv-sdk:0.1.0")
    
    // Kotlinx Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    
    // Ktor Client (JVM)
    implementation("io.ktor:ktor-client-java:3.4.2")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}
