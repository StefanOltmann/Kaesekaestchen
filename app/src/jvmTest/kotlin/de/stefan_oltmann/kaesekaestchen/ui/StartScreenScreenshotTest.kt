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

import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.russhwolf.settings.MapSettings
import java.util.Locale
import kotlin.test.Test
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

/**
 * Golden screenshots of the start screen, in both locales and themes.
 *
 * @param testLocale Locale pinned while the variant is captured.
 * @param localeSuffix Locale suffix of the golden file name.
 * @param darkTheme True to render the dark theme.
 * @param themeSuffix Theme suffix of the golden file name.
 */
@RunWith(Parameterized::class)
class StartScreenScreenshotTest(
    private val testLocale: Locale,
    private val localeSuffix: String,
    private val darkTheme: Boolean,
    private val themeSuffix: String
) {

    @get:Rule
    val localeRule = FixedLocaleTestRule(testLocale)

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val screenshotRule = ScreenshotTestRule()

    @Test
    fun renderStartScreen() {

        composeTestRule.setContent {

            ScreenshotFrame(darkTheme = darkTheme) {

                StartScreen(
                    storage = MapSettings(),
                    onPlayClick = { _, _ -> }
                )
            }
        }

        composeTestRule.captureScreenshot("start-screen-$localeSuffix-$themeSuffix")
    }

    companion object {

        @JvmStatic
        @Parameters(name = "{1}-{3}")
        fun screenshotVariants(): Collection<Array<Any>> = screenshotVariantParameters()
    }
}
