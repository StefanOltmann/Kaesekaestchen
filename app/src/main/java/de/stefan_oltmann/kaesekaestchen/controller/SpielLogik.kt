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
package de.stefan_oltmann.kaesekaestchen.controller

import de.stefan_oltmann.kaesekaestchen.model.SpielModus
import de.stefan_oltmann.kaesekaestchen.model.Spieler
import de.stefan_oltmann.kaesekaestchen.model.Spielfeld
import de.stefan_oltmann.kaesekaestchen.model.Strich
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Diese Klasse ist für Behandlung des Spielablaufs zuständig.
 */
class SpielLogik private constructor(val spielfeld: Spielfeld) {

    private lateinit var callback: SpielLogikCallback

    /* Job um Hintergrund-Aktionen durchzuführen. */
    private var coroutinesJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + coroutinesJob)

    private val spielerManager = SpielerManager()

    private val aktuellerSpieler
        get() = spielerManager.aktuellerSpieler

    private var zugWirdGeradeAusgefuehrt: Boolean = false

    constructor(spielfeld: Spielfeld, spielModus: SpielModus) : this(spielfeld) {

        if (spielModus == SpielModus.EINZELSPIELER)
            spielerManager.bestimmeZufaelligComputerGegner()
    }

    fun start(callback: SpielLogikCallback) {

        this.callback = callback

        callback.onSpielerIstAnDerReihe(aktuellerSpieler)

        if (spielerManager.isComputerGegner(aktuellerSpieler))
            fuehreKiZugDurch()
    }

    /*
     * Wenn das ViewModel vernichtet wird, soll auch der Job
     * abgebrochen werden, bevor es ansonsten dadurch zu Fehlern kommt.
     */
    fun onCleared() {
        coroutinesJob.cancel()
    }

    fun behandleSpielerEingabe(strich: Strich) {

        /* Bereits vergebene Striche können nicht ausgewählt werden. */
        if (strich.besitzer != null)
            return

        /*
         * Diese Abfrage soll verhindern, dass ein Anwender,
         * der während des KI-Zugs wild rumklickt diesen stören kann.
         */
        if (zugWirdGeradeAusgefuehrt)
            return

        try {

            zugWirdGeradeAusgefuehrt = true

            /*
             * Führe die Aktion für den Spieler durch
             */
            waehleStrichFuerAktuellenSpielerUndCheckBeendet(strich)

            if (spielerManager.isComputerGegner(aktuellerSpieler))
                fuehreKiZugDurch()

        } finally {

            zugWirdGeradeAusgefuehrt = false
        }
    }

    private fun fuehreKiZugDurch() {

        if (!spielerManager.isComputerGegner(aktuellerSpieler))
            throw IllegalStateException("Soll nur aufgerufen werden, wenn die KI dran ist.")

        /*
         * Führe auf einem separaten Thread die Antwort des Computer-Gegners
         * durch. Dies passiert hier separat vom UI Thread, damit der Thread
         * zwischendrin schlafen gelegt und der Anwender die Aktionen der KI
         * mitverfolgen kann während parallel die UI aktualisiert wird.
         */
        uiScope.launch {

            while (!isSpielBeendet() && spielerManager.isComputerGegner(aktuellerSpieler)) {

                /* Der Spieler soll die Aktion der KI sehen. */
                delay(500)

                /*
                 * FIXME Das sollte nicht vorkommen können, aber hin und wieder tritt
                 * durch irgendeinen Umstand dieses Problem auf.
                 * Dies ist erstmal ein Workaround.
                 */
                if (!spielfeld.isEsGibtFreieStriche())
                    break

                val kiZugStrich = spielfeld.ermittleGutenStrichFuerComputerZug()

                waehleStrichFuerAktuellenSpielerUndCheckBeendet(kiZugStrich)
            }
        }
    }

    private fun waehleStrichFuerAktuellenSpielerUndCheckBeendet(strich: Strich) {

        callback.onSpielerIstAnDerReihe(aktuellerSpieler)

        val kaestchenKonnteGeschlossenWerden =
            spielfeld.waehleStrich(strich, aktuellerSpieler)

        callback.aktualisiereSpielfeldViewAnzeige()

        /*
         * War es das?
         */
        checkSpielBeendet()

        /*
         * Wenn ein Kästchen geschlossen werden konnte, ist derjenige Spieler
         * noch einmal dran. Konnte er keines schließen, ist der andere Spieler
         * wieder dran.
         */
        if (!kaestchenKonnteGeschlossenWerden)
            spielerManager.waehleNaechstenSpielerAus()

        callback.onSpielerIstAnDerReihe(aktuellerSpieler)
    }

    private fun checkSpielBeendet() {

        if (!isSpielBeendet())
            return

        uiScope.launch {

            /*
             * Noch eine Sekunde warten, damit der Spieler sich in Ruhe
             * die Endsituation anschauen kann.
             */
            delay(1000)

            val gewinner = ermittleSpielerMitHoechsterPunktZahl()

            callback.onSpielBeendet(
                gewinner,
                spielfeld.ermittlePunktzahl(Spieler.KAESE),
                spielfeld.ermittlePunktzahl(Spieler.MAUS)
            )
        }
    }

    /*
     * Das Spiel ist dann beendet, wenn alle Kästchen einen Besitzer haben
     */
    private fun isSpielBeendet() = spielfeld.isAlleKaestchenHabenBesitzer()

    private fun ermittleSpielerMitHoechsterPunktZahl(): Spieler {

        var gewinner: Spieler? = null
        var maxPunktZahl = 0

        for (spieler in spielerManager.spieler) {

            val punktZahl = spielfeld.ermittlePunktzahl(spieler)

            if (punktZahl > maxPunktZahl) {
                gewinner = spieler
                maxPunktZahl = punktZahl
            }
        }

        return gewinner!!
    }
}
