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

/**
 * Thread-local registration of the [ScreenshotTestRule] that currently runs.
 *
 * Capturing helpers resolve the active rule through this context so call
 * sites stay free of rule plumbing. The context is per thread, which keeps
 * the registration correct even when a future test runner executes classes
 * in parallel.
 */
internal object ScreenshotRuleContext {

    private val threadLocal = ThreadLocal<ScreenshotTestRule>()

    /**
     * Rule that currently runs on this thread, or null when none is active.
     */
    var current: ScreenshotTestRule?
        get() = threadLocal.get()
        set(value) = threadLocal.set(value)
}
