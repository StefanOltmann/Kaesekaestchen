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
package de.stefan_oltmann.kaesekaestchen

import androidx.compose.runtime.Composable

/**
 * Web has no system back gesture, so nothing is handled.
 *
 * @param enabled True while the caller would consume back gestures.
 * @param onBack Callback invoked on a back gesture while enabled.
 */
@Composable
actual fun SystemBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {

    /* The web platform has no system back gesture. */
}
