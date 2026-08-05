import java.io.File
import java.nio.file.Paths

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    alias(libs.plugins.android.application)
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

base {
    val vName = libs.versions.versionName.get()
    val vCode = libs.versions.versionCode.get()
    archivesName.set("FunHouse-v$vName-($vCode)")
}

android {
    namespace = "com.gepetto.gamescollection"
    compileSdk = libs.versions.compileSdk.get().toInt()

    signingConfigs {
        create("release") {
            storeFile = file(project.findProperty("gepetto.store_file") as String? ?: "release.keystore")
            storePassword = project.findProperty("gepetto.store_psw") as String?
            keyAlias = project.findProperty("gepetto.key_alias") as String?
            keyPassword = project.findProperty("gepetto.key_psw") as String?
        }
    }

    defaultConfig {
        applicationId = "com.gepetto.gamescollection"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt() * 10 + 1
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }

    bundle {
        language {
            enableSplit = false
        }
    }
}

val generateCommonConfig = tasks.register("generateCommonConfig") {
    val vName = libs.versions.versionName.get()
    val vCode = libs.versions.versionCode.get().toLong()
    val isWindows = System.getProperty("os.name").lowercase().contains("win")
    val desktopCode = if (isWindows) vCode * 10 + 5 else vCode * 10 + 4
    val outputDir = layout.buildDirectory.dir("generated/commonConfig/kotlin").get().asFile
    val outputFile = File(outputDir, "com/gepetto/gamescollection/CommonConfig.kt")
    
    inputs.property("versionName", vName)
    inputs.property("versionCode", vCode)
    outputs.dir(outputDir)

    doLast {
        outputFile.parentFile.mkdirs()
        outputFile.writeText("""
            package com.gepetto.gamescollection

            object CommonConfig {
                const val versionName = "$vName"
                  const val desktopVersionCode = ${desktopCode}L
                const val webVersionCode = ${vCode*10+6}L
            }
        """.trimIndent())
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                "-opt-in=androidx.compose.animation.ExperimentalSharedTransitionApi",
                "-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
            )
        }
    }
    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                "-opt-in=androidx.compose.animation.ExperimentalSharedTransitionApi",
                "-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
            )
        }
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(generateCommonConfig)
            dependencies {
                implementation(project(":shared:common"))

                // Game features
                implementation(project(":feature:blackjack"))
                implementation(project(":feature:castle-kotlin"))
                implementation(project(":feature:chess"))
                implementation(project(":feature:chimaera-kotlin"))
                implementation(project(":feature:classic-arcades"))
                implementation(project(":feature:colossal-cave-adventure-kotlin"))
                implementation(project(":feature:craps"))
                implementation(project(":feature:dinkum-kotlin"))
                implementation(project(":feature:eliza-kotlin"))
                implementation(project(":feature:funhouse-engine-kotlin"))
                implementation(project(":feature:hangman-kotlin"))
                implementation(project(":feature:mistery-mansion-kotlin"))
                implementation(project(":feature:poker"))
                implementation(project(":feature:roulette"))
                implementation(project(":feature:secret-forest-kotlin"))
                implementation(project(":feature:slot-machine"))
                implementation(project(":feature:space-wars-kotlin"))
                implementation(project(":feature:tetric"))
                implementation(project(":feature:wander-engine-kotlin"))
                implementation(project(":feature:wizards-castle-kotlin"))

                implementation(libs.kotlinx.collections.immutable)
                implementation(libs.kotlinx.serialization.json)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(libs.jetbrains.compose.ui.tooling.preview)

                implementation(libs.adaptive)
                implementation(libs.adaptive.layout)
                implementation(libs.adaptive.navigation)
                implementation(libs.material3.adaptive.navigation.suite)
                implementation(libs.androidx.navigation3.runtime)
                implementation(libs.androidx.navigation3.ui)
                implementation(libs.androidx.lifecycle.viewmodel.navigation3)
                implementation(compose.animation)
                implementation(compose.materialIconsExtended)

                implementation(libs.gepetto.utils)
                implementation(libs.gepetto.gclog)
                implementation(libs.gepetto.adslib)
                implementation(libs.circum)
                implementation(libs.koin.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.core.ktx)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.compose.ui.tooling)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
        val wasmJsMain by getting {
            dependencies {
            }
        }
    }
}

val desktopMajor = libs.versions.versionName.get().split(".").getOrElse(0) { "2" }
val desktopMinor = libs.versions.versionName.get().split(".").getOrElse(1) { "0" }
val desktopBuildNum = libs.versions.versionCode.get()
val desktopPackageVersion = "$desktopMajor.$desktopMinor.$desktopBuildNum"

compose.desktop {
    application {
        mainClass = "MainKt"
        javaHome = run {
            val envJavaHome = System.getenv("JAVA_HOME")
            if (!envJavaHome.isNullOrEmpty() && File(envJavaHome, "bin/jpackage").exists()) {
                return@run envJavaHome
            }
            val currentJavaHome = System.getProperty("java.home")
            if (File(currentJavaHome, "bin/jpackage").exists()) {
                return@run currentJavaHome
            }
            try {
                val process = ProcessBuilder("/usr/libexec/java_home").start()
                val path = process.inputStream.bufferedReader().use { it.readText().trim() }
                if (path.isNotEmpty() && File(path, "bin/jpackage").exists()) {
                    return@run path
                }
            } catch (e: Exception) {}
            val brewJavaHome = "/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home"
            if (File(brewJavaHome, "bin/jpackage").exists()) {
                return@run brewJavaHome
            }
            currentJavaHome
        }

        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Pkg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe
            )
            packageName = "FunHouse"
            packageVersion = desktopPackageVersion
            modules("jdk.crypto.ec")
            
            macOS {
                packageName = "FunHouse"
                iconFile.set(project.file("src/desktopMain/resources/icons/icon.icns"))
                bundleID = "com.gepetto.gamescollection"
            }
            windows {
                packageName = "FunHouse"
                iconFile.set(project.file("src/desktopMain/resources/icons/icon.ico"))
                shortcut = true
                menu = true
                upgradeUuid = "ef53cf5d-16be-4ef0-9e0a-36fb88dc2287"
            }
        }
    }
}

tasks.matching { it.name == "packageDmg" }.configureEach {
    doFirst {
        val resourcesDir = project.file("build/compose/tmp/resources")
        println("[VolumeIconHook] packageDmg doFirst started. Deleting resources directory.")
        if (resourcesDir.exists()) {
            resourcesDir.deleteRecursively()
        }
        val sourceIcon = project.file("packaging/macos/FunHouse-volume.icns")
        if (sourceIcon.exists()) {
            Thread {
                val startTime = System.currentTimeMillis()
                val timeout = 300000L
                var copied = false
                val targetIcon = File(resourcesDir, "FunHouse-volume.icns")
                val triggerFile = File(resourcesDir, "Info.plist")
                while (System.currentTimeMillis() - startTime < timeout) {
                    if (triggerFile.exists()) {
                        Thread.sleep(50)
                        try {
                            sourceIcon.copyTo(targetIcon, overwrite = true)
                            println("[VolumeIconHook] Successfully copied icon to ${targetIcon.absolutePath}")
                            copied = true
                            break
                        } catch (e: Exception) {
                            Thread.sleep(50)
                        }
                    }
                    Thread.sleep(20)
                }
                if (!copied) {
                    println("[VolumeIconHook] Failed to copy icon: timeout or trigger not found")
                }
            }.start()
        } else {
            println("[VolumeIconHook] Source icon not found at ${sourceIcon.absolutePath}")
        }
    }
    doLast {
        val resourcesDir = project.file("build/compose/tmp/resources")
        val targetIcon = File(resourcesDir, "FunHouse-volume.icns")
        println("[VolumeIconHook] Target icon exists at the end: ${targetIcon.exists()}")
        if (resourcesDir.exists()) {
            println("[VolumeIconHook] Files at the end: ${resourcesDir.list()?.joinToString()}")
        }
        val dmgDir = File(layout.buildDirectory.get().asFile, "compose/binaries/main/dmg")
        val generatedFile = File(dmgDir, "FunHouse-$desktopPackageVersion.dmg")
        val targetFile = File(dmgDir, "funhouse.dmg")
        if (generatedFile.exists()) {
            if (targetFile.exists()) {
                targetFile.delete()
            }
            if (generatedFile.renameTo(targetFile)) {
                println("Renamed DMG to ${targetFile.name}")
            } else {
                println("Failed to rename DMG")
            }
        }
    }
}

tasks.matching { it.name == "packageMsi" }.configureEach {
    doLast {
        val msiDir = File(layout.buildDirectory.get().asFile, "compose/binaries/main/msi")
        val generatedFile = File(msiDir, "FunHouse-$desktopPackageVersion.msi")
        val targetFile = File(msiDir, "funhouse.msi")
        if (generatedFile.exists()) {
            if (targetFile.exists()) {
                targetFile.delete()
            }
            if (generatedFile.renameTo(targetFile)) {
                println("Renamed MSI to ${targetFile.name}")
            } else {
                println("Failed to rename MSI")
            }
        }
    }
}

tasks.withType<org.jetbrains.compose.desktop.application.tasks.AbstractJPackageTask>().configureEach {
    if (name.contains("Msi", ignoreCase = true)) {
        freeArgs.add("--resource-dir")
        freeArgs.add(project.file("wix").absolutePath)
    }
}
