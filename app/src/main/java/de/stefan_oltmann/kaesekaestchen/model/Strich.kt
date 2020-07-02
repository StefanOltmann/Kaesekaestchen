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
class Strich(private val kaestchenOben: Kaestchen?,
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

    var besitzer: Spieler? = null

//    fun getKaestchenListe(): List<Kaestchen> {
//        return Collections.unmodifiableList(kaestchenListe)
//    }

    /**
     * Wenn eines der Kästchen um diesen Strich nur noch zwei Besitzer hat, dann
     * h#tte es nach dem Setzen dieses Striches nur noch einen... Damit würde
     * man dem Gegner ein Kästchen schenken.
     */
    fun isKoennteUmliegendendesKaestchenSchliessen(): Boolean {

        for (kaestchen in kaestchenListe)
            if (kaestchen.stricheOhneBesitzer.size <= 2)
                return true

        return false
    }

    override fun toString(): String {
        return ("Strich [kaestchenOben=" + kaestchenOben + ", kaestchenUnten="
                + kaestchenUnten + ", kaestchenLinks=" + kaestchenLinks
                + ", kaestchenRechts=" + kaestchenRechts + ", besitzer="
                + besitzer + "]")
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (kaestchenLinks?.hashCode() ?: 0)
        result = prime * result + (kaestchenOben?.hashCode() ?: 0)
        result = prime * result + (kaestchenRechts?.hashCode() ?: 0)
        result = prime * result + (kaestchenUnten?.hashCode() ?: 0)
        return result
    }

    override fun equals(obj: Any?): Boolean {

        if (this === obj)
            return true

        if (obj == null)
            return false

        if (javaClass != obj.javaClass)
            return false

        val other = obj as Strich

        if (kaestchenLinks == null) {
            if (other.kaestchenLinks != null)
                return false

        } else if (kaestchenLinks != other.kaestchenLinks)
            return false

        if (kaestchenOben == null) {
            if (other.kaestchenOben != null)
                return false
        } else if (kaestchenOben != other.kaestchenOben)
            return false

        if (kaestchenRechts == null) {
            if (other.kaestchenRechts != null)
                return false
        } else if (kaestchenRechts != other.kaestchenRechts)
            return false

        if (kaestchenUnten == null) {
            if (other.kaestchenUnten != null)
                return false
        } else if (kaestchenUnten != other.kaestchenUnten)
            return false

        return true
    }
}