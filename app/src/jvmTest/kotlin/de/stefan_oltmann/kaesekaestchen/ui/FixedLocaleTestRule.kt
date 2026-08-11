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

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.Locale

/**
 * Pins the JVM default locale for the duration of a test.
 *
 * Compose Multiplatform resolves string resources on desktop from the
 * default locale at composition time. Screenshot goldens must not depend on
 * the host machine's locale, so every screenshot test pins the same locale
 * and restores the previous one afterwards.
 *
 * @param locale Locale to pin during the test.
 */
class FixedLocaleTestRule(
    private val locale: Locale
) : TestRule {

    override fun apply(base: Statement, description: Description): Statement =
        object : Statement() {

            override fun evaluate() {

                val previousLocale = Locale.getDefault()

                Locale.setDefault(locale)

                try {

                    base.evaluate()

                } finally {

                    Locale.setDefault(previousLocale)
                }
            }
        }
}
