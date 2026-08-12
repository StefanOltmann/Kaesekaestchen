/*
 * Kaesekaestchen
 * A simple Dots'n'Boxes Game for Android
 *
 * Copyright (C) Stefan Oltmann
 *
 * This file is part of Kaesekaestchen.
 *
 * Kaesekaestchen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kaesekaestchen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kaesekaestchen. If not, see <http://www.gnu.org/licenses/>.
 */
package de.stefan_oltmann.kaesekaestchen.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.fail
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * JUnit4 rule that records, verifies, or smoke-runs golden screenshots.
 *
 * The mode is selected by the `kaesekaestchen.screenshot.mode` system
 * property, which the `recordScreenshots` and `verifyScreenshots` Gradle
 * tasks set:
 * - `record`: write the captured image into the golden directory.
 * - `verify`: compare the captured image with the stored golden and fail on
 *   any pixel difference.
 * - any other or missing value: smoke mode where the capture only renders,
 *   so a plain `gradle test` never writes or compares machine-specific
 *   goldens.
 *
 * The golden directory defaults to `src/jvmTest/screenshots` and can be
 * overridden with the `kaesekaestchen.screenshot.dir` system property.
 */
class ScreenshotTestRule(
    private val goldenRootDir: Path = resolveGoldenRootDir()
) : TestRule {

    private var mode: String? = null

    /**
     * Register this rule as the capture target of the current test thread.
     *
     * The registration is thread-local so capturing helpers can resolve the
     * active rule without passing it through every call site.
     */
    override fun apply(base: Statement, description: Description): Statement =
        object : Statement() {

            override fun evaluate() {

                mode = resolveMode()

                val previous = ScreenshotRuleContext.current

                ScreenshotRuleContext.current = this@ScreenshotTestRule

                try {
                    base.evaluate()
                } finally {
                    ScreenshotRuleContext.current = previous
                }
            }
        }

    /**
     * Record, verify, or ignore the given image under the golden directory.
     *
     * @param image Captured frame to store or compare.
     * @param name Golden file name relative to the golden root, without `.png`.
     */
    fun capture(image: ImageBitmap, name: String) {

        val goldenFile = goldenRootDir.resolve("$name.png")

        when (mode) {

            MODE_RECORD -> {
                Files.createDirectories(goldenFile.parent)
                Files.write(goldenFile, encodePng(image))
            }

            MODE_VERIFY -> verifyAgainstGolden(image, goldenFile)

            else -> Unit
        }
    }

    /**
     * Fail the test when the captured frame differs from the stored golden.
     */
    private fun verifyAgainstGolden(image: ImageBitmap, goldenFile: Path) {

        if (!Files.isRegularFile(goldenFile))
            fail("Golden screenshot missing: $goldenFile. Run the recordScreenshots task first.")

        val golden = decodePng(Files.readAllBytes(goldenFile))

        val actualPixels = image.toPixelMap()
        val goldenPixels = golden.toPixelMap()

        assertSameSize(actualPixels, goldenPixels, goldenFile)

        val diffCount = countDifferingPixels(actualPixels, goldenPixels)

        if (diffCount > 0)
            fail("Golden screenshot mismatch for $goldenFile: $diffCount differing pixels.")
    }

    /**
     * Fail when the captured and stored image dimensions differ.
     *
     * @param actual Pixels of the captured frame.
     * @param golden Pixels of the stored golden.
     * @param goldenFile Golden file used in the failure message.
     */
    private fun assertSameSize(actual: PixelMap, golden: PixelMap, goldenFile: Path) {

        if (actual.width == golden.width && actual.height == golden.height)
            return

        fail(
            "Golden screenshot size mismatch for $goldenFile: " +
                "expected ${golden.width}x${golden.height}, " +
                "got ${actual.width}x${actual.height}."
        )
    }

    /**
     * Count the pixels that differ between the captured frame and the golden.
     *
     * @param actual Pixels of the captured frame.
     * @param golden Pixels of the stored golden.
     * @return Number of pixels whose color differs.
     */
    private fun countDifferingPixels(actual: PixelMap, golden: PixelMap): Int {

        var diffCount = 0

        for (y in 0 until actual.height)
            for (x in 0 until actual.width)
                if (actual[x, y] != golden[x, y])
                    diffCount++

        return diffCount
    }

    companion object {

        /** System property that selects the golden screenshot mode. */
        private const val SCREENSHOT_MODE_PROPERTY = "kaesekaestchen.screenshot.mode"

        /** System property that points at the golden screenshot directory. */
        private const val SCREENSHOT_DIR_PROPERTY = "kaesekaestchen.screenshot.dir"

        /** Mode that writes the captured frames into the golden directory. */
        private const val MODE_RECORD = "record"

        /** Mode that compares the captured frames against the golden files. */
        private const val MODE_VERIFY = "verify"

        /**
         * Resolve the golden directory from the system property, defaulting
         * to the repository layout used by the Gradle tasks.
         */
        private fun resolveGoldenRootDir(): Path {

            val configured = System.getProperty(SCREENSHOT_DIR_PROPERTY)

            return if (configured != null)
                Paths.get(configured)
            else
                Paths.get("src", "jvmTest", "screenshots")
        }

        /**
         * Resolve the screenshot mode from the system property.
         */
        private fun resolveMode(): String? =
            System.getProperty(SCREENSHOT_MODE_PROPERTY)?.lowercase()

        /**
         * Encode a captured frame as PNG bytes.
         */
        private fun encodePng(image: ImageBitmap): ByteArray {

            val encoded = Image.makeFromBitmap(image.asSkiaBitmap())
                .encodeToData(EncodedImageFormat.PNG)
                ?: error("Failed to encode golden screenshot as PNG.")

            return encoded.bytes
        }

        /**
         * Decode PNG bytes back into a compose image for comparison.
         */
        private fun decodePng(bytes: ByteArray): ImageBitmap =
            Image.makeFromEncoded(bytes).toComposeImageBitmap()
    }
}
