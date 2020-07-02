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

import java.util.*

/**
 * Der SpielerManager bestimmt, welcher Spieler am Zug ist und wählt den
 * nächsten Spieler aus. Er kennt alle Mitspieler.
 *
 * @author Stefan Oltmann
 */
class SpielerManager {

    /** Liste aller Spieler.  */
    private val spielerListe: MutableList<Spieler> = mutableListOf()

    /** Der Spieler, der gerade am Zug ist.  */
    private var aktuellerSpieler: Spieler? = null

    fun addSpieler(spieler: Spieler) {
        spielerListe.add(spieler)
    }

    val spieler: List<Spieler>
        get() = Collections.unmodifiableList(spielerListe)

    fun getAktuellerSpieler(): Spieler {

        if (aktuellerSpieler == null)
            throw RuntimeException("Vor Abfrage des Spielers muss 'neuerZug' mindestens einmal aufgerufen worden sein!")

        return aktuellerSpieler as Spieler
    }

    fun waehleNaechstenSpielerAus() {

        val indexAktuellerSpieler = spielerListe.indexOf(aktuellerSpieler)

        var indexNaechsterSpieler = indexAktuellerSpieler + 1

        /* Starte am Ende der Liste wieder vorne. */
        if (indexNaechsterSpieler > spielerListe.size - 1)
            indexNaechsterSpieler = 0

        aktuellerSpieler = spielerListe[indexNaechsterSpieler]
    }
}