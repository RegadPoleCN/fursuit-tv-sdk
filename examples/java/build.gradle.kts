plugins {
    java
    kotlin("jvm") version "2.1.20"
    application
}

group = "com.furrist.rp.furtv.sdk.example"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.furrist.rp:fursuit-tv-sdk:0.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass.set("com.furrist.rp.furtv.sdk.example.Main")
}

tasks.named<JavaExec>("run") {
    jvmArgs = listOf("-Dfile.encoding=UTF-8")
}
