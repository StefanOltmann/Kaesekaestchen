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

/**
 * Das Spielprinzip besteht daraus, Striche zu setzen um Kästchen zu schließen.
 * Diese Klasse repräsentiert einen solchen Strich.
 *
 * @author Stefan Oltmann
 */
data class Strich(private val kaestchenOben: Kaestchen?,
                  private val kaestchenUnten: Kaestchen?,
                  private val kaestchenLinks: Kaestchen?,
                  private val kaestchenRechts: Kaestchen?) {

    /* Auflistung zum Durch-iterieren */
    private val kaestchenListe: MutableList<Kaestchen> = mutableListOf()

    init {
        kaestchenOben?.let { kaestchenListe.add(it) }
        kaestchenUnten?.let { kaestchenListe.add(it) }
        kaestchenLinks?.let { kaestchenListe.add(it) }
        kaestchenRechts?.let { kaestchenListe.add(it) }
    }

    /**
     * Ein Kästchen hat zu Beginn noch keinen Besitzer
     */
    var besitzer: Spieler? = null

    /**
     * Wenn eines der Kästchen um diesen Strich nur noch zwei Besitzer hat, dann
     * hätte es nach dem Setzen dieses Striches nur noch einen... Damit würde
     * man dem Gegner ein Kästchen schenken.
     */
    fun isKoennteUmliegendendesKaestchenSchliessen(): Boolean {

        for (kaestchen in kaestchenListe)
            if (kaestchen.stricheOhneBesitzer.size <= 2)
                return true

        return false
    }
}