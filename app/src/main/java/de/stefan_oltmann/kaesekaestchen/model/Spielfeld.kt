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
 * Das Spielfeld.
 *
 * @author Stefan Oltmann
 */
class Spielfeld private constructor() {

    private lateinit var feldGroesse: FeldGroesse

    val breiteInKaestchen
        get() = feldGroesse.groesseX

    val hoeheInKaestchen
        get() = feldGroesse.groesseX

    private lateinit var kaestchenArray: Array<Array<Kaestchen?>>

    /*
     * Die Liste aller Kästchen
     */
    private val _kaestchenListe: MutableList<Kaestchen> = mutableListOf()

    val kaestchenListe : List<Kaestchen>
        get() = _kaestchenListe

    /**
     * Aus Performance-Gründen wird eine zweite Liste geführt, damit die
     * 'kaestchenListe' nicht so häufig durch-iteriert werden muss. Dies führt
     * bei großen Feldern zu schlechter Performance.
     */
    private val offeneKaestchen: MutableList<Kaestchen> = mutableListOf()

    private val stricheOhneBesitzer: MutableSet<Strich> = mutableSetOf()

    constructor(feldGroesse: FeldGroesse): this() {

        this.feldGroesse = feldGroesse

        val breiteInKaestchen = feldGroesse.groesseX
        val hoeheInKaestchen = feldGroesse.groesseY

        kaestchenArray = Array(breiteInKaestchen) { arrayOfNulls<Kaestchen?>(hoeheInKaestchen) }

        /*
         * Erstmal alle Kästchen erzeugen und in das Array sowie
         * die Listen einfügen.
         */
        for (rasterX in 0 until breiteInKaestchen) {
            for (rasterY in 0 until hoeheInKaestchen) {

                val kaestchen = Kaestchen(rasterX, rasterY)

                kaestchenArray[kaestchen.rasterX][kaestchen.rasterY] = kaestchen

                _kaestchenListe.add(kaestchen)
                offeneKaestchen.add(kaestchen)
            }
        }

        /**
         * Jetzt die Beziehungen zueinander herstellen um
         * gemeinsame Striche der Kästchen zu ermitteln.
         */
        for (kaestchen in _kaestchenListe) {

            val rasterX = kaestchen.rasterX
            val rasterY = kaestchen.rasterY

            /*
             * Nach rechts
             */

            var kaestchenRechts: Kaestchen? = null

            if (rasterX < breiteInKaestchen - 1)
                kaestchenRechts = kaestchenArray[rasterX + 1][rasterY]

            if (kaestchenRechts != null) {

                val strichRechts = Strich(null, null, kaestchen, kaestchenRechts)

                kaestchen.strichRechts = strichRechts
                kaestchenRechts.strichLinks = strichRechts
                stricheOhneBesitzer.add(strichRechts)
            }

            /*
             * Nach unten
             */

            var kaestchenUnten: Kaestchen? = null

            if (rasterY < hoeheInKaestchen - 1)
                kaestchenUnten = kaestchenArray[rasterX][rasterY +1]

            if (kaestchenUnten != null) {

                val strichUnten = Strich(kaestchen, kaestchenUnten, null, null)

                kaestchen.strichUnten = strichUnten
                kaestchenUnten.strichOben = strichUnten
                stricheOhneBesitzer.add(strichUnten)
            }
        }
    }

    fun getKaestchen(rasterX: Int, rasterY: Int): Kaestchen {

        /* Außerhalb des Rasters gibts kein Kästchen. */
        if (!isImRaster(rasterX, rasterY))
            throw IllegalArgumentException("Das Kästchen liegt außerhalb des Rasters: $rasterX >= $feldGroesse.groesseX || $rasterY >= $feldGroesse.groesseY")

        return kaestchenArray[rasterX][rasterY]!!
    }

    fun isImRaster(rasterX: Int, rasterY: Int) =
        rasterX < feldGroesse.groesseX && rasterY < feldGroesse.groesseY

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

    fun isEsGibtFreieStriche() = stricheOhneBesitzer.isNotEmpty()

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
            return it
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

    private fun findeLetztenOffenenStrichFuerKaestchen(): Strich? {

        for (kaestchen in offeneKaestchen)
            if (kaestchen.stricheOhneBesitzer.size == 1)
                return kaestchen.stricheOhneBesitzer[0]

        return null
    }

    private fun findeZufaelligenStrich(): Strich {

        if (!isEsGibtFreieStriche())
            throw IllegalStateException("Es gibt keine freien Striche mehr.")

        val stricheOhneBesitzer = stricheOhneBesitzer.toList()

        val zufallsZahl = (0..stricheOhneBesitzer.size.minus(1)).random()

        return stricheOhneBesitzer[zufallsZahl]
    }

    fun ermittlePunktzahl(spieler: Spieler): Int {

        var punkte = 0

        for (kaestchen in _kaestchenListe)
            if (kaestchen.besitzer == spieler)
                punkte++

        return punkte
    }
}