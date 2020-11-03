/*
 * Kaesekaestchen
 * A simple Dots'n'Boxes Game for Android
 *
 * Copyright (C) Stefan Oltmann
 *
 * Contact : dotsandboxes@stefan-oltmann.de
 * Homepage: https://github.com/StefanOltmann/Kaesekaestchen
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

/**
 * Ein Kästchen auf dem @{link Spielfeld}.
 */
data class Kaestchen(
    val rasterX: Int,
    val rasterY: Int) {

    /**
     * Konnte ein Spieler ein Kästchen schließen, wird er der Besitzer des
     * Kästchens. Dies zählt am Ende des Spiels als 1 Siegpunkt.
     */
    var besitzer: Spieler? = null

    /* Striche des Kästchens */
    var strichOben: Strich? = null
    var strichUnten: Strich? = null
    var strichLinks: Strich? = null
    var strichRechts: Strich? = null

    val stricheOhneBesitzer: List<Strich>
        get() {

            val striche: MutableList<Strich> = mutableListOf()

            strichOben?.let { if (it.besitzer == null) striche.add(it) }
            strichUnten?.let { if (it.besitzer == null) striche.add(it) }
            strichLinks?.let { if (it.besitzer == null) striche.add(it) }
            strichRechts?.let { if (it.besitzer == null) striche.add(it) }

            return striche
        }

    val isAlleStricheHabenBesitzer: Boolean
        get() = stricheOhneBesitzer.isEmpty()
}