plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    application
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.furrist.rp:fursuit-tv-sdk:0.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.furrist.rp.furtv.sdk.example.MainKt")
}
