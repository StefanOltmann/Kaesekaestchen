import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

version = "0.7.7"

android {

    namespace = "de.stefan_oltmann.kaesekaestchen.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {

        applicationId = "de.stefan_oltmann.kaesekaestchen"

        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        versionName = "0.7.7"
        versionCode = 19
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {

            /*
             * The app is open source, so obfuscation would not protect
             * anything. The release build stays unminified.
             */
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
        allWarningsAsErrors = true
    }
    jvmToolchain(17)
}

dependencies {
    implementation(project(":app"))
    implementation(libs.androidx.activity.compose)
}
