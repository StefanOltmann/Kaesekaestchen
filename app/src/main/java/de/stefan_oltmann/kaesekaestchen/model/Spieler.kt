/*
 * Kaesekaestchen
 * A simple Dots'n'Boxes Game for Android
 *
 * Copyright (C) Stefan Oltmann
 *
 * Contact : dotsandboxes@stefan-oltmann.de
 * Homepage: http://www.stefan-oltmann.de/
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
package de.stefan_oltmann.kaesekaestchen.model

import android.graphics.drawable.Drawable

/**
 * Diese Klasse repräsentiert einen Spieler. Dieser hat einen Namen, eine Farbe
 * und Symbol. Er kann entweder menschlich sein oder die KI.
 *
 * @author Stefan Oltmann
 */
data class Spieler(val name: String,
                   val symbol: Drawable,
                   val farbe: Int,
                   val spielerTyp: SpielerTyp) {

    val isComputerGegner: Boolean
        get() = spielerTyp == SpielerTyp.COMPUTER
}