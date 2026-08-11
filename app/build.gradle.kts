import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kover)
    alias(libs.plugins.valkyrie)
    alias(libs.plugins.msix)
    alias(libs.plugins.android.git.version)
}

/*
 * The version comes from the newest git tag (v0.7.7 -> 0.7.7), so version
 * bumps are tag creations and never touch this file.
 */
androidGitVersion {
    format = "%tag%"
    prefix = "v"
}

group = "de.stefan_oltmann.kaesekaestchen"
version = androidGitVersion.name()

logger.lifecycle("App version $version (Code: ${androidGitVersion.code()})")

detekt {
    source.setFrom(fileTree("src"), files("build.gradle.kts"))
    config.setFrom(rootProject.file("detekt.yml"))
    allRules = true
    parallel = true
}

valkyrie {

    packageName = "de.stefan_oltmann.kaesekaestchen.icons"

    outputDirectory = layout.buildDirectory.dir("generated/valkyrie")

    resourceDirectoryName = "iconResources"

    generateAtSync = true

    autoMirror = false

    codeStyle {
        useExplicitMode = false
        indentSize = 4
    }

    iconPack {
        name = "AppIcon"
        targetSourceSet = "commonMain"
        useFlatPackage = false
        autoMirror = true
    }
}

dependencies {
    detektPlugins(libs.detekt.compose.rules)
}

kotlin {

    compilerOptions {

        /* Make the code safer */
        progressiveMode = true
        extraWarnings = true
        allWarningsAsErrors = true
    }

    jvmToolchain(jdkVersion = 25)

    jvm()

    android {

        namespace = "de.stefan_oltmann.kaesekaestchen"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        /*
         * Package composeResources into Android assets (CMP-9547, required with the
         * com.android.kotlin.multiplatform.library plugin).
         */
        androidResources.enable = true

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        withHostTest {}
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {

        outputModuleName = "app"

        browser {

            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path

            commonWebpackConfig {
                outputFileName = "app.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    /* Serve sources to debug inside browser. */
                    static(rootDirPath)
                    static(projectDirPath)
                }
            }
        }

        binaries.executable()
    }

    sourceSets {

        sourceSets["commonMain"].kotlin.srcDirs(
            file("build/generated/src/commonMain/kotlin/")
        )

        commonMain.dependencies {

            /* Compose UI */
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.material3)
            api(libs.compose.ui)
            api(libs.compose.components.resources)

            /* Coroutines */
            implementation(libs.kotlinx.coroutines.core)

            /* Settings */
            implementation(libs.multiplatform.settings)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.multiplatform.settings.test)
        }

        androidMain.dependencies {
            /* Pin the Android Compose runtime used by the Android target. */
            implementation(libs.androidx.runtime.android)
            /* Provides the back dispatcher used by SystemBackHandler. */
            implementation(libs.androidx.activity.compose)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }

        jvmTest.dependencies {
            implementation(libs.kotlin.test.junit)
            implementation(compose.desktop.currentOs)
            implementation(libs.compose.ui.test.junit4)
        }
    }
}

compose.desktop {

    application {

        mainClass = "de.stefan_oltmann.kaesekaestchen.MainKt"

        buildTypes {
            release {
                proguard {
                    /*
                     * The app is open source, so obfuscation would not
                     * protect anything. ProGuard still has to process the
                     * jars, but without renaming or optimizing.
                     */
                    version.set("7.8.2")
                    obfuscate.set(false)
                    optimize.set(false)
                }
            }
        }

        nativeDistributions {

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe)

            /* Fix crash on systems with active accessibility */
            modules("jdk.accessibility")

            packageName = "Kaesekaestchen"

            if (androidGitVersion.code() == 0) {

                /* Values for the dev version. */
                packageVersion = "1.0.0"

            } else {

                packageVersion = version.toString()
            }

            macOS {
                iconFile.set(project.file("../icon/icon.icns"))
            }

            windows {
                iconFile.set(project.file("../icon/icon.ico"))
            }

            linux {
                iconFile.set(project.file("../icon/icon.png"))
            }
        }
    }
}

// region MSIX
msix {

    manifest {
        appId.set("Kaesekaestchen")
        displayName.set("Käsekästchen")
        description.set("A classic Dots'n'Boxes game")
        identityName.set("StefanOltmann.Kaesekaestchen")
        publisher.set("CN=1A06AF6C-2943-4BE6-BB85-12677BA3F28D")
        publisherDisplayName.set("Stefan Oltmann")
        version.set(androidGitVersion.name() + ".0")
        processorArchitecture.set("x64")
        appExecutable.set("Kaesekaestchen.exe")
    }
}
// endregion

// region BuildInfo.kt
project.afterEvaluate {

    logger.lifecycle("Generate BuildInfo.kt")

    val outputDir = layout.buildDirectory.file("generated/src/commonMain/kotlin").get().asFile

    outputDir.mkdirs()

    outputDir.resolve("BuildInfo.kt").printWriter().use { writer ->

        writer.println("const val APP_VERSION: String = \"$version\"")

        writer.flush()
    }
}
// endregion

// region Golden screenshot tasks
/**
 * Register a task that runs only the golden screenshot tests.
 *
 * The screenshot tests in src/jvmTest only compose (smoke) during a plain
 * `gradle test`; goldens are machine-specific, so recording and pixel
 * verification run only through the dedicated tasks below.
 *
 * @param name Task name used on the command line.
 * @param mode Golden mode passed to the tests through a system property.
 */
fun registerScreenshotTestTask(name: String, mode: String): TaskProvider<Test> =
    tasks.register<Test>(name) {

        description = "Run the golden screenshot tests in $mode mode."
        group = "verification"

        testClassesDirs = sourceSets["jvmTest"].output.classesDirs
        classpath = sourceSets["jvmTest"].runtimeClasspath

        /* Only the screenshot test classes participate in these runs. */
        filter {
            includeTestsMatching("*ScreenshotTest")
        }

        systemProperty("kaesekaestchen.screenshot.mode", mode)
        systemProperty(
            "kaesekaestchen.screenshot.dir",
            file("src/jvmTest/screenshots").absolutePath
        )

        /* Recorded goldens feed the verify run's up-to-date check. */
        inputs.dir(file("src/jvmTest/screenshots"))

        if (mode == "record") {
            /* Recording always re-runs so developers can refresh goldens. */
            outputs.upToDateWhen { false }
        }
    }

registerScreenshotTestTask("recordScreenshots", "record")
registerScreenshotTestTask("verifyScreenshots", "verify")
// endregion

// region Code coverage
kover {
    reports {
        filters {
            excludes {
                classes(
                    /* Desktop entry point that opens a real window. */
                    "de.stefan_oltmann.kaesekaestchen.MainKt",
                    "de.stefan_oltmann.kaesekaestchen.ComposableSingletons*",
                    /* Platform settings actuals that never execute in JVM tests. */
                    "de.stefan_oltmann.kaesekaestchen.SettingsProvider",
                    "de.stefan_oltmann.kaesekaestchen.Platform*",
                    "de.stefan_oltmann.kaesekaestchen.*android*",
                    "de.stefan_oltmann.kaesekaestchen.android.*",
                    /* The UI, the generated Valkyrie icons and Compose resources. */
                    "de.stefan_oltmann.kaesekaestchen.ui.*",
                    "de.stefan_oltmann.kaesekaestchen.icons.*",
                    "de.stefan_oltmann.kaesekaestchen.app.generated.*",
                    /* The generated version constant. */
                    "BuildInfo*"
                )
            }
        }

        total {
            verify {
                rule {
                    /*
                     * Enforce a high line-coverage bound on every check run.
                     * The bound is intentionally below 100%: coverage-chasing
                     * tests for trivial rendering are not worth their noise,
                     * and the suite guards every behavior that can
                     * meaningfully fail.
                     */
                    minBound(95)
                }
            }
        }
    }
}
// endregion

// region Cleanup stale JARs
/*
 * `jvmJar` names its output after the project version (`version =
 * androidGitVersion.name()` above), so every version bump leaves the
 * previous jar in `app/build/libs` forever - Gradle never prunes old
 * outputs. This task deletes every `app-jvm-*` jar that does not belong to
 * the current version (including old `-sources`/`-javadoc` siblings) and
 * runs right after `jvmJar`, so the pruning itself never touches
 * incremental compilation or test outputs: the current version's jar is
 * kept by prefix match, anything else named `app-jvm-*` is stale by
 * definition.
 */
val pruneStaleBuildLibs = tasks.register("pruneStaleBuildLibs") {

    description = "Deletes versioned jars from app/build/libs that belong to an older build."
    group = "build"

    val libsDirectory = layout.buildDirectory.dir("libs")
    val currentJarPrefix = "app-jvm-$version"

    doLast {

        val directory = libsDirectory.get().asFile

        if (!directory.isDirectory)
            return@doLast

        directory.listFiles { file ->
            file.isFile &&
                file.name.startsWith("app-jvm-") &&
                !file.name.startsWith(currentJarPrefix)
        }?.forEach { staleJar ->

            if (staleJar.delete())
                logger.lifecycle("Pruned stale build jar: ${staleJar.name}")
            else
                logger.warn("Could not delete stale build jar (file in use?): ${staleJar.name}")
        }
    }
}

gradle.projectsEvaluated {
    /*
     * jvmJar is created by the Kotlin plugin during its own configuration, so the finalizedBy
     * wiring must wait until every plugin has registered its tasks - see pruneStaleBuildLibs'
     * doc comment above.
     */
    tasks.named("jvmJar") {
        finalizedBy(pruneStaleBuildLibs)
    }
}
// endregion
