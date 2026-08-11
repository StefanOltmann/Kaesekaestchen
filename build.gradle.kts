/*
 * Hoisted plugin versions for all subprojects.
 *
 * Every plugin is declared here with `apply false` so it is resolved once on
 * the root build classpath; the subprojects apply the same aliases without
 * triggering a separate Kotlin Gradle plugin load per subproject.
 */

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.valkyrie) apply false
    alias(libs.plugins.msix) apply false
    alias(libs.plugins.android.git.version) apply false
}
