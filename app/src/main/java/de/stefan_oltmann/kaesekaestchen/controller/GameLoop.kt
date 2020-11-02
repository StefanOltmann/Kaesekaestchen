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
package de.stefan_oltmann.kaesekaestchen.controller

import de.stefan_oltmann.kaesekaestchen.model.*
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class GameLoop(val callback: GameLoopCallback) {

    private val lock = ReentrantLock()
    private val condition : Condition = lock.newCondition()

    lateinit var spielfeld: Spielfeld

    private val spielerManager = SpielerManager()

    /** Diese Variable steuert den Game Loop Thread.  */
    @Volatile
    private var running = true

    /**
     * Über die letzte Eingabe wird in Erfahrung gebracht, was der Nutzer
     * möchte. Der Abruf dieses Wertes ist sozusagen im Spiel-Ablauf blocking.
     */
    @Volatile
    var letzteEingabe: Strich? = null
        private set

    fun stop() {
        running = false
    }

    fun resetLetzteEingabe() {
        letzteEingabe = null
    }

    fun behandleNutzerEingabe(strich: Strich) {

        /*
         * An dieser Stelle hat der Benutzer seine Eingabe erfolgreich getätigt.
         * Wir schreiben seine Eingabe in eine Zwischenvariable die zur
         * Kommunikation mit dem Gameloop-Thread verwendet wird und wecken
         * diesen via "notifyAll" wieder auf. Der Gameloop-Thread wurde zuvor
         * mit "await()" auf der Condition pausiert.
         */
        letzteEingabe = strich

        lock.withLock {
            condition.signalAll()
        }
    }

    fun start(spielModus: SpielModus, feldGroesse: FeldGroesse) {

        if (spielModus == SpielModus.EINZELSPIELER)
            spielerManager.bestimmeZufaelligComputerGegner()

        spielfeld = Spielfeld.SpielfeldFactory.generiere(feldGroesse)

        val thread = Thread(GameLoopRunnable())

        thread.start()

        running = true
    }

    private inner class GameLoopRunnable : Runnable {

        override fun run() {

            /* Auswahl des ersten Spielers */
            spielerManager.waehleNaechstenSpielerAus()

            while (!isSpielBeendet()) {

                val spieler = spielerManager.getAktuellerSpieler()

                callback.onSpielerIstAnDerReihe(spieler)

                var eingabeStrich: Strich?

                if (!spielerManager.isComputerGegner(spieler)) {

                    resetLetzteEingabe()

                    /*
                     * Der Benutzer muss nun seine Eingabe tätigen. Dieser
                     * Gameloop- Thread soll nun darauf warten.
                     */
                    while (letzteEingabe.also { eingabeStrich = it } == null) {

                        lock.withLock {
                            condition.await()
                        }
                    }

                } else {

                    try {
                        /* Der Nutzer soll die Aktion der KI sehen. */
                        Thread.sleep(500)
                    } catch (ignore: InterruptedException) {
                        /* Ignorieren. */
                    }

                    eingabeStrich = spielfeld.ermittleGutenStrichFuerComputerZug()
                }

                waehleStrich(eingabeStrich!!)

                /*
                 * Wurde die Activity beendet, dann auch diesen Thread stoppen.
                 * Ohne diese Zeile würde die Activity niemals enden und der
                 * Thread immer weiter laufen, bis Android diesen killt. Wir
                 * wollen aber natürlich nicht negativ auffallen.
                 */
                if (!running)
                    return
            }

            /*
             * Wenn alle Kästchen besetzt sind, ist das Spiel vorbei und der
             * "Game Score" kann angezeigt werden.
             */
            if (isSpielBeendet()) {

                /* Noch eine Sekunde warten, um das Ende zu sehen. */
                Thread.sleep(1000)

                val gewinner = ermittleSpielerMitHoechsterPunktZahl()

                callback.onSpielBeendet(gewinner,
                    spielfeld.ermittlePunktzahl(Spieler.KAESE),
                    spielfeld.ermittlePunktzahl(Spieler.MAUS))
            }
        }
    }

    private fun waehleStrich(strich: Strich) {

        /* Bereits vergebene Striche können nicht ausgewählt werden. */
        if (strich.besitzer != null)
            return

        val aktuellerSpieler = spielerManager.getAktuellerSpieler()

        val kaestchenKonnteGeschlossenWerden =
            spielfeld.waehleStrich(strich, aktuellerSpieler)

        /*
         * Wenn ein Kästchen geschlossen werden konnte, ist derjenige Spieler
         * noch einmal dran. Konnte er keines schließen, ist der andere Spieler
         * wieder dran.
         */
        if (!kaestchenKonnteGeschlossenWerden)
            spielerManager.waehleNaechstenSpielerAus()

        callback.aktualisiereSpielfeldViewAnzeige()
    }

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