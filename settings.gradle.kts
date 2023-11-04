pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://plugins.gradle.org/m2/")
    }
}
//enableFeaturePreview("VERSION_CATALOGS")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

//    versionCatalogs {
//        create("libs") { from(files("./gradle/libs.versions.toml")) }

//        create("libs") {
//            val kotlinVersion = "1.9.10"
//            // use jdk17
//            version("jdkVersion", JavaVersion.VERSION_17.majorVersion)
//            version("kotlinVersion", kotlinVersion)
//
//            version("android.compileSdk", "34")
//            version("android.targetSdk", "34")
//            version("android.buildToolsVersion", "34.0.0")
//            version("android.minSdk", "26")
//
//            library("android.gradle", "com.android.tools.build:gradle:8.1.2")
//            plugin("android.library", "com.android.library").version("8.1.2")
//            plugin("android.application", "com.android.application").version("8.1.2")
//
//            // 当前 android 项目 kotlin 的版本
//            library(
//                "kotlin.gradle.plugin",
//                "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
//            )
//            library(
//                "kotlin.serialization",
//                "org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion"
//            )
//            library(
//                "kotlin.stdlib.common", "org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion"
//            )
//            plugin("kotlin.serialization", "org.jetbrains.kotlin.plugin.serialization").version(
//                kotlinVersion
//            )
//            plugin("kotlin.parcelize", "org.jetbrains.kotlin.plugin.parcelize").version(
//                kotlinVersion
//            )
//            plugin("kotlin.kapt", "org.jetbrains.kotlin.kapt").version(kotlinVersion)
//            plugin("kotlin.multiplatform", "org.jetbrains.kotlin.multiplatform").version(
//                kotlinVersion
//            )
//            plugin("kotlin.android", "org.jetbrains.kotlin.android").version(kotlinVersion)
//            // https://mvnrepository.com/artifact/androidx.compose.compiler/compiler
//            version("compose.compilerVersion", "1.5.3")
//            val composeVersion = "1.5.4"
//            library("compose.ui", "androidx.compose.ui:ui:$composeVersion")
//            library("compose.preview", "androidx.compose.ui:ui-tooling-preview:$composeVersion")
//            library("compose.tooling", "androidx.compose.ui:ui-tooling:$composeVersion")
//            library("compose.junit4", "androidx.compose.ui:ui-test-junit4:$composeVersion")
//            library("compose.material3", "androidx.compose.material3:material3:1.1.2")
//            library("compose.activity", "androidx.activity:activity-compose:1.7.2")
//
//            // https://github.com/LSPosed/AndroidHiddenApiBypass
//            library("lsposed.hiddenapibypass", "org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
//
//            // 工具集合类
//            // https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/README-CN.md
//            library("others.utilcodex", "com.blankj:utilcodex:1.31.1")
//
//            // https://dylancaicoding.github.io/ActivityResultLauncher/#/
//            library(
//                "others.activityResultLauncher",
//                "com.github.DylanCaiCoding:ActivityResultLauncher:1.1.2"
//            )
//            // json5
//            // https://github.com/falkreon/Jankson
//            library("others.jankson", "blue.endless:jankson:1.2.3")
//
//            // https://github.com/TorryDo/Floating-Bubble-View
//            library("others.floating.bubble.view", "io.github.torrydo:floating-bubble-view:0.6.3")
//
//            library("androidx.appcompat", "androidx.appcompat:appcompat:1.6.1")
//            library("androidx.core.ktx", "androidx.core:core-ktx:1.12.0")
//            library(
//                "androidx.lifecycle.runtime.ktx", "androidx.lifecycle:lifecycle-runtime-ktx:2.6.2"
//            )
//            library("androidx.junit", "androidx.test.ext:junit:1.1.5")
//            library("androidx.espresso", "androidx.test.espresso:espresso-core:3.5.1")
//
//            // https://developer.android.com/jetpack/androidx/releases/room
//            val roomVersion = "2.6.0"
//            library("androidx.room.runtime", "androidx.room:room-runtime:$roomVersion")
//            library("androidx.room.compiler", "androidx.room:room-compiler:$roomVersion")
//            library("androidx.room.ktx", "androidx.room:room-ktx:$roomVersion")
//
//            library("androidx.splashscreen", "androidx.core:core-splashscreen:1.0.1")
//
//            library(
//                "google.accompanist.drawablepainter",
//                "com.google.accompanist:accompanist-drawablepainter:0.32.0"
//            )
//
//            library("junit", "junit:junit:4.13.2")
//            library(
//                "kotlinx.serialization.json",
//                "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0"
//            )
//
//            // https://github.com/Kotlin/kotlinx.collections.immutable
//            library(
//                "kotlinx.collections.immutable",
//                "org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.6"
//            )
//
////            https://developer.android.com/reference/kotlin/org/json/package-summary
//            library("org.json", "org.json:json:20210307")
//
//            plugin("google.ksp", "com.google.devtools.ksp").version("1.9.10-1.0.13")
//
//            plugin("google.hilt", "com.google.dagger.hilt.android").version("2.48.1")
//            library("google.hilt.android", "com.google.dagger:hilt-android:2.48.1")
//            library(
//                "google.hilt.android.compiler",
//                "com.google.dagger:hilt-android-compiler:2.48.1"
//            )
//            library(
//                "androidx.hilt.navigation.compose", "androidx.hilt:hilt-navigation-compose:1.0.0"
//            )
//        }
//    }
}

rootProject.name = "BleMsg"
include(":app")
 