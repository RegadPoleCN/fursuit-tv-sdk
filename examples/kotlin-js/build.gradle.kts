plugins {
    kotlin("js") version "2.1.20"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.furrist.rp:fursuit-tv-sdk:0.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
}

kotlin {
    js {
        nodejs()
        binaries.executable()
    }
}
