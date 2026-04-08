import dev.detekt.gradle.Detekt

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
    `maven-publish`
}

repositories {
    mavenCentral()
}

group = "me.regadpole"
version = "1.0-SNAPSHOT"

kotlin {
    applyDefaultHierarchyTemplate()
    explicitApi()
    
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js {
        nodejs {
            testTask {
                useMocha()
            }
        }
        binaries.library()
    }

    // Native targets - conditionally enabled
    // Note: On Windows, Native compilation may fail due to security policies
    // blocking Kotlin Native DLL files. Set skipNative=true to skip Native targets.
    val skipNative = project.hasProperty("skipNative") && project.property("skipNative").toString().toBoolean()
    
    if (!skipNative) {
        // iOS targets
        iosX64()
        iosArm64()
        iosSimulatorArm64()
        
        // macOS targets
        macosX64()
        macosArm64()
        
        // Linux targets
        linuxX64()
        linuxArm64()
        
        // Mingw targets
        mingwX64()
        
        // Android Native targets
        androidNativeArm32()
        androidNativeArm64()
        androidNativeX86()
        androidNativeX64()
    }

    sourceSets {
        commonMain {
            dependencies {
                // API - 只暴露必要的依赖
                // kotlinx.coroutines 需要暴露，因为 SDK 使用 suspend 函数
                api(libs.kotlinx.coroutines.core)
                
                // Implementation - 内部使用，不暴露给使用者
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                
                // OAuth 回调服务器 - CIO 引擎
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.cio)
                implementation(libs.ktor.server.status.pages)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.ktor.client.java)
                implementation(kotlin("test-junit5"))
            }
        }

        jsTest {
            dependencies {
                implementation(libs.ktor.client.js)
                implementation(kotlin("test-js"))
            }
        }
    }
}

// Configure detekt
detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.from(files("detekt-config.yml"))
    source.from(files("src"))
}

tasks.withType<Detekt>().configureEach {
    reports {
        checkstyle.required.set(true)
        html.required.set(true)
        sarif.required.set(true)
        markdown.required.set(true)
    }
}

// Maven Publish Configuration
publishing {
    publications {
        withType<MavenPublication> {
            pom {
                name.set("fursuit-tv-sdk")
                description.set("Cross-platform SDK for Fursuit.TV API built with Kotlin Multiplatform")
                url.set("https://github.com/RegadPoleCN/fursuit-tv-sdk")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("regadpole")
                        name.set("RegadPole")
                        email.set("1651233735@qq.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/RegadPoleCN/fursuit-tv-sdk.git")
                    developerConnection.set("scm:git:ssh://github.com/RegadPoleCN/fursuit-tv-sdk.git")
                    url.set("https://github.com/RegadPoleCN/fursuit-tv-sdk")
                }
            }
        }
    }
}
