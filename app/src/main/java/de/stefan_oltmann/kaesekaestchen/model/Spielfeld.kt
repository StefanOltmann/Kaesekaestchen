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
 * Das Spielfeld.
 *
 * @author Stefan Oltmann
 */
class Spielfeld private constructor(
    val breiteInKaestchen: Int,
    val hoeheInKaestchen: Int) {

    private val kaestchenArray: Array<Array<Kaestchen?>> =
        Array(breiteInKaestchen) { arrayOfNulls<Kaestchen?>(hoeheInKaestchen) }

    /**
     * Aus Performance-Gründen wird eine zweite Liste geführt, damit die
     * 'kaestchenListe' nicht so häufig durch-iteriert werden muss. Dies führt
     * bei großen Feldern zu schlechter Performance.
     */
    private val offeneKaestchen: MutableList<Kaestchen> = mutableListOf()

    val offeneKaestchenUnmodifiable: List<Kaestchen>
        get() = Collections.unmodifiableList(offeneKaestchen)

    private val stricheOhneBesitzer: MutableSet<Strich> = mutableSetOf()

    val stricheOhneBesitzerUnmodifiable: Set<Strich>
        get() = Collections.unmodifiableSet(stricheOhneBesitzer)

    val kaestchenListe: List<Kaestchen>
        get() {

            val liste: MutableList<Kaestchen> = mutableListOf()

            for (rasterX in 0 until breiteInKaestchen)
                for (rasterY in 0 until hoeheInKaestchen)
                    liste.add(kaestchenArray[rasterX][rasterY]!!)

            return liste.toList()
        }

    private fun addKaestchen(kaestchen: Kaestchen) {
        kaestchenArray[kaestchen.rasterX][kaestchen.rasterY] = kaestchen
        offeneKaestchen.add(kaestchen)
    }

    private fun addStrich(strich: Strich) {
        stricheOhneBesitzer.add(strich)
    }

    fun isImRaster(rasterX: Int, rasterY: Int) =
        rasterX < breiteInKaestchen && rasterY < hoeheInKaestchen

    fun getKaestchen(rasterX: Int, rasterY: Int): Kaestchen {

        /* Außerhalb des Rasters gibts kein Kästchen. */
        if (!isImRaster(rasterX, rasterY))
            throw IllegalArgumentException("Das Kästchen liegt außerhalb des Rasters: $rasterX >= $breiteInKaestchen || $rasterY >= $hoeheInKaestchen")

        return kaestchenArray[rasterX][rasterY]!!
    }

    /**
     * Schließt alle Kästchen, die geschlossen werden können.
     *
     * @param zuzuweisenderBesitzer
     *      Der zuzuweisende Besitzer dieser Kästchen
     *
     * @return Konnte mindestens ein Kästchen geschlossen werden? (Wichtig für Spielablauf)
     */
    private fun schliesseAlleMoeglichenKaestchen(zuzuweisenderBesitzer: Spieler): Boolean {

        var mindestensEinKaestchenKonnteGeschlossenWerden = false

        val offeneKaestchenIt = offeneKaestchen.iterator()

        while (offeneKaestchenIt.hasNext()) {

            val kaestchen = offeneKaestchenIt.next()

            if (kaestchen.isAlleStricheHabenBesitzer && kaestchen.besitzer == null) {

                kaestchen.besitzer = zuzuweisenderBesitzer

                offeneKaestchenIt.remove()

                mindestensEinKaestchenKonnteGeschlossenWerden = true
            }
        }

        return mindestensEinKaestchenKonnteGeschlossenWerden
    }

    fun isAlleKaestchenHabenBesitzer() = offeneKaestchen.isEmpty()

    fun waehleStrich(strich: Strich, spieler: Spieler): Boolean {

        strich.besitzer = spieler

        stricheOhneBesitzer.remove(strich)

        return schliesseAlleMoeglichenKaestchen(spieler)
    }

    fun ermittleGutenStrichFuerComputerZug(): Strich {

        /*
         * Wenn irgendwo ein Kästchen geschlossen werden kann, dann
         * soll das natürlich auf jeden Fall passieren. Alles andere
         * wäre ja wirklich sehr dumm.
         */
        findeLetztenOffenenStrichFuerKaestchen()?.let {
            return it;
        }

        /*
         * Falls kein Kästchen irgendwo geschlossen werden kann probieren
         * wir jetzt zufällig Striche durch und achten darauf, dass wir
         * dabei keine Punkte verschenken. Wenn wir aber nach 30 Versuchen
         * nichts gefunden haben, muss es wohl so sein.
         */

        var zufallsStrich = findeZufaelligenStrich()

        var loopCounter = 0

        while (zufallsStrich.isKoennteUmliegendendesKaestchenSchliessen()) {

            zufallsStrich = findeZufaelligenStrich()

            if (++loopCounter >= 30)
                break
        }

        return zufallsStrich
    }

    fun findeLetztenOffenenStrichFuerKaestchen(): Strich? {

        for (kaestchen in offeneKaestchenUnmodifiable)
            if (kaestchen.stricheOhneBesitzer.size == 1)
                return kaestchen.stricheOhneBesitzer[0]

        return null
    }

    fun findeZufaelligenStrich(): Strich {

        val stricheOhneBesitzer = stricheOhneBesitzerUnmodifiable.toList()

        val zufallsZahl = (0..stricheOhneBesitzer.size.minus(1)).random()

        return stricheOhneBesitzer[zufallsZahl]
    }

    fun ermittlePunktzahl(spieler: Spieler): Int {

        var punkte = 0

        for (kaestchen in kaestchenListe)
            if (kaestchen.besitzer == spieler)
                punkte++

        return punkte
    }

    object SpielfeldFactory {

        /**
         * Factory Method zur Erzeugung eines Spielfeldes
         */
        fun generiere(feldGroesse: FeldGroesse): Spielfeld {

            val anzahlH = feldGroesse.groesseX
            val anzahlV = feldGroesse.groesseY

            val spielfeld = Spielfeld(anzahlH, anzahlV)

            for (rasterX in 0 until anzahlH)
                for (rasterY in 0 until anzahlV)
                    spielfeld.addKaestchen(Kaestchen(rasterX, rasterY))

            for (rasterX in 0 until anzahlH) {
                for (rasterY in 0 until anzahlV) {

                    val kaestchen = spielfeld.getKaestchen(rasterX, rasterY)

                    var kaestchenUnten: Kaestchen? = null
                    var kaestchenRechts: Kaestchen? = null

                    if (rasterY < anzahlV - 1) kaestchenUnten =
                        spielfeld.getKaestchen(rasterX, rasterY + 1)

                    if (rasterX < anzahlH - 1) kaestchenRechts =
                        spielfeld.getKaestchen(rasterX + 1, rasterY)

                    val strichUnten = Strich(kaestchen, kaestchenUnten, null, null)

                    val strichRechts = Strich(null, null, kaestchen, kaestchenRechts)

                    if (kaestchenRechts != null) {
                        kaestchen.strichRechts = strichRechts
                        kaestchenRechts.strichLinks = strichRechts
                        spielfeld.addStrich(strichRechts)
                    }

                    if (kaestchenUnten != null) {
                        kaestchen.strichUnten = strichUnten
                        kaestchenUnten.strichOben = strichUnten
                        spielfeld.addStrich(strichUnten)
                    }
                }
            }

            return spielfeld
        }
    }
}