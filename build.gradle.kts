import dev.petuska.npm.publish.extension.domain.NpmAccess

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.npm.publish)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.suspend.transform)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.maven.publish)
    signing
}

repositories {
    mavenCentral()
}

group = "com.furrist.rp"
version = libs.versions.fursuit.tv.sdk.get()

kotlin {
    applyDefaultHierarchyTemplate()
    explicitApi()

    compilerOptions {
        freeCompilerArgs.addAll(
            "-XXLanguage:+JsAllowExportingSuspendFunctions",
            "-opt-in=kotlin.js.ExperimentalJsExport",
        )
    }

    jvm()
    jvmToolchain(17)

    // ESM target - for modern bundlers and ES module consumers
    js {
        generateTypeScriptDefinitions()
        compilerOptions {
            useEsClasses.set(true)
            moduleKind.set(org.jetbrains.kotlin.gradle.dsl.JsModuleKind.MODULE_ES)
            sourceMap.set(true)
            target.set("es2015")
            freeCompilerArgs.add("-Xes-long-as-bigint")
        }
        browser {
            commonWebpackConfig {
                cssSupport { enabled.set(true) }
//                output?.libraryTarget = "umd"
            }
        }
        nodejs()
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
                api(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
            }
        }

        // Only JVM target supports callback server (based on CIO engine)
        jvmMain {
            dependencies {
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.cio)
                implementation(libs.ktor.server.status.pages)
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.ktor.client.mock)
                implementation(libs.ktor.client.java)
            }
        }

        jsMain {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }

        nativeMain {
            dependencies {
                implementation(libs.ktor.network)
            }
        }
    }
}

suspendTransformPlugin {
    enabled = true
    includeAnnotation = true
    includeRuntime = true
    transformers {
        useDefault()
    }
}

npmPublish {
    organization = "regadpole"
    access = NpmAccess.PUBLIC

    registries {
        register("npmjs") {
            uri("https://registry.npmjs.org")
            authToken.set(project.findProperty("npmToken") as String? ?: "")
        }
    }

    packages {
        named("js") {
            packageName = "fursuit-tv-sdk"
            readme = file("README.md")
            version = libs.versions.fursuit.tv.sdk.get()
            packageJson {
                license = "MIT"
                description = "Cross-platform SDK for Fursuit.TV API built with Kotlin Multiplatform"
                homepage = "https://github.com/RegadPoleCN/fursuit-tv-sdk"
                keywords =
                    listOf(
                        "fursuit-tv",
                        "furtv",
                        "sdk",
                        "kotlin",
                        "kmp",
                        "api",
                        "furry",
                        "multiplatform",
                    )
                repository {
                    type = "git"
                    url = "https://github.com/RegadPoleCN/fursuit-tv-sdk.git"
                }
                author {
                    name = "RegadPole"
                    email = "1651233735@qq.com"
                }
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
    parallel = true
}

// Configure Dokka for API documentation（Dokka 2 扩展 DSL，作用于 dokkaGenerate 任务；
// 输出保持默认 build/dokka/html，与 docs workflow 的 Pages 上传路径一致。
// include 文件须为 DCL 格式（# Module / # Package 头），不能用 README.md）
dokka {
    dokkaSourceSets {
        configureEach {
            includes.from("docs/dokka-module.md")

            perPackageOption {
                matchingRegex.set(".*\\.internal.*")
                suppress.set(true)
            }
        }
    }
}

// Configure ktlint
ktlint {
    version.set("1.0.0")
    verbose.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(true)
    enableExperimentalRules.set(false)

    filter {
        exclude("**/generated/**")
        include("**/commonMain/**", "**/jvmMain/**", "**/jsMain/**", "**/nativeMain/**", "**/jvmTest/**")
    }
}

// Configure API compatibility validator
// Removed in refactor (binary-compatibility-validator plugin removed).

// Custom tasks for better development experience
tasks.register("checkAll") {
    group = "verification"
    description = "Runs all code quality checks"
    dependsOn(tasks.named("detekt"))
    dependsOn(tasks.named("ktlintCheck"))
}

tasks.register("quickBuild") {
    group = "build"
    description = "Builds JVM and JS targets only (faster)"
    dependsOn(tasks.named("jvmJar"))
    dependsOn(tasks.named("jsJar"))
}

// Maven Publish Configuration
mavenPublishing {
    signing.useGpgCmd()
    publishToMavenCentral()
    signAllPublications()

    coordinates("com.furrist.rp", "fursuit-tv-sdk", project.version.toString())

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
