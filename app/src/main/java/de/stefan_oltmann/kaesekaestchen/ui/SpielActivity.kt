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
package de.stefan_oltmann.kaesekaestchen.ui

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.stefan_oltmann.kaesekaestchen.R
import de.stefan_oltmann.kaesekaestchen.model.*
import de.stefan_oltmann.kaesekaestchen.model.Spielfeld.Companion.generiere
import java.util.*
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Die Haupt-Activty, die das Spielfeld verwaltet und den Gameloop steuert.
 */
class SpielActivity : AppCompatActivity() {

    private val lock = ReentrantLock()
    private val condition : Condition = lock.newCondition()

    private val spielfeldView: SpielfeldView
        get() = findViewById<View>(R.id.spielfeldView) as SpielfeldView

    private val aktuellerSpielerImageView
        get() = findViewById<View>(R.id.aktuellerSpielerSymbol) as ImageView

    private val punkteAnzeigeTextView
        get() = findViewById<View>(R.id.punkteAnzeige) as TextView

    private var spielfeld: Spielfeld? = null

    private val spielerManager = SpielerManager()

    private val handler = Handler(Looper.myLooper()!!)

    /** Diese Variable steuert den Game Loop Thread.  */
    @Volatile
    private var running = true

    public override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_spiel)

        val intentExtras = intent.extras!!
        val spielerTyp1 = intentExtras["spielerTyp1"] as SpielerTyp
        val spielerTyp2 = intentExtras["spielerTyp2"] as SpielerTyp
        val feldGroesseX = intentExtras.getInt("feldGroesseX")
        val feldGroesseY = intentExtras.getInt("feldGroesseY")

        spielfeld = generiere(feldGroesseX, feldGroesseY)

        spielfeldView.init(spielfeld, lock, condition)

        spielerManager.addSpieler(
            Spieler(
                resources.getString(R.string.spieler_1_name),
                ContextCompat.getDrawable(applicationContext, R.drawable.ic_spieler_symbol_kaese)!!,
                ContextCompat.getColor(applicationContext, R.color.spieler_1_farbe),
                spielerTyp1
            )
        )

        spielerManager.addSpieler(
            Spieler(
                resources.getString(R.string.spieler_2_name),
                ContextCompat.getDrawable(applicationContext, R.drawable.ic_spieler_symbol_maus)!!,
                ContextCompat.getColor(applicationContext, R.color.spieler_2_farbe),
                spielerTyp2
            )
        )

        startGameLoop()
    }

    override fun onStop() {
        running = false
        super.onStop()
    }

    private fun startGameLoop() {

        val thread = Thread(GameLoopRunnable())

        thread.start()

        running = true
    }

    private inner class GameLoopRunnable : Runnable {

        override fun run() {

            /* Auswahl des ersten Spielers */
            spielerManager.waehleNaechstenSpielerAus()

            while (!isGameOver()) {

                val spieler = spielerManager.getAktuellerSpieler()

                /*
                 * Anzeige welcher Spieler dran ist und wieviele Punkt dieser
                 * schon hat.
                 */
                handler.post {

                    aktuellerSpielerImageView.setImageDrawable(spieler.symbol)
                    punkteAnzeigeTextView.text = ermittlePunktzahl(spieler).toString()
                }

                var eingabe: Strich?

                if (!spieler.isComputerGegner) {

                    spielfeldView.resetLetzteEingabe()

                    /*
                     * Der Benutzer muss nun seine Eingabe tätigen. Dieser
                     * Gameloop- Thread soll nun darauf warten.
                     */
                    while (spielfeldView.letzteEingabe.also { eingabe = it } == null) {

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

                    eingabe = fuehreKiGegnerZugAus(spieler.spielerTyp)
                }

                waehleStrich(eingabe!!)

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
            if (isGameOver())
                showGameOverDialog()
        }
    }

    private fun showGameOverDialog() {

        handler.post {

            val gewinner = ermittleGewinner()

            // FIXME Unflexibel
            val pokalBildId =
                if (gewinner.name == resources.getString(R.string.spieler_1_name)) R.drawable.ic_pokal_kaese else R.drawable.ic_pokal_maus

            val alertDialog =
                AlertDialog.Builder(this@SpielActivity)
                    .setTitle(resources.getText(R.string.game_score))
                    .setIcon(ContextCompat.getDrawable(applicationContext, pokalBildId))
                    .setMessage(createGameOverDialogMessage())
                    .setCancelable(false)
                    .setPositiveButton(resources.getText(R.string.play_again))
                    { _, _ ->
                        startActivity(intent)
                    }
                    .setNegativeButton(resources.getText(R.string.zurueck_zum_hauptmenue))
                    { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                    .create()

            alertDialog.show()
        }
    }

    private fun createGameOverDialogMessage() : String {

        val gewinner = ermittleGewinner()

        val sb = StringBuilder()

        sb.append(resources.getString(R.string.gewinner))
        sb.append(": ")
        sb.append(gewinner.name)
        sb.appendLine()

        for (spieler in spielerManager.spieler) {

            sb.append(spieler.name)
            sb.append(":\t\t")
            sb.append(ermittlePunktzahl(spieler))
            sb.appendLine()
        }

        return sb.toString()
    }

    private fun fuehreKiGegnerZugAus(spielerTyp: SpielerTyp): Strich {

        val strich = waehleLetztenOffenenStrichFuerKaestchen()

        if (strich != null)
            return strich

        var zufallsStrich = waehleZufallsStrich()

        var loopCounter = 0

        while (zufallsStrich.isKoennteUmliegendendesKaestchenSchliessen()) {

            zufallsStrich = waehleZufallsStrich()

            /*
             * Dies wird maximal 30 Mal versucht. Konnte dann immer noch
             * keine gefunden werden, gibt es entweder keine mehr oder der
             * Gegner darf auch mal Glück haben.
             */
            if (++loopCounter >= 30)
                break
        }

        return zufallsStrich
    }

    private fun waehleLetztenOffenenStrichFuerKaestchen(): Strich? {

        for (kaestchen in spielfeld!!.offeneKaestchenUnmodifiable)
            if (kaestchen.stricheOhneBesitzer.size == 1)
                return kaestchen.stricheOhneBesitzer[0]

        return null
    }

    private fun waehleZufallsStrich(): Strich {

        val stricheOhneBesitzer = spielfeld!!.stricheOhneBesitzerUnmodifiable.toList()

        val zufallsZahl = Random().nextInt(stricheOhneBesitzer.size)

        return stricheOhneBesitzer[zufallsZahl]
    }

    private fun waehleStrich(strich: Strich) {

        /* Bereits vergebene Striche können nicht ausgewählt werden. */
        if (strich.besitzer != null)
            return

        val aktuellerSpieler = spielerManager.getAktuellerSpieler()

        val kaestchenKonnteGeschlossenWerden =
            spielfeld!!.waehleStrich(strich, aktuellerSpieler)

        /*
         * Wenn ein Kästchen geschlossen werden konnte, ist derjenige Spieler
         * noch einmal dran. Konnte er keines schließen, ist der andere Spieler
         * wieder dran.
         */
        if (!kaestchenKonnteGeschlossenWerden)
            spielerManager.waehleNaechstenSpielerAus()

        spielfeldView.aktualisiereAnzeige()
    }

    fun isGameOver(): Boolean {
        return spielfeld!!.isAlleKaestchenHabenBesitzer()
    }

    fun ermittleGewinner(): Spieler {

        var gewinner: Spieler? = null
        var maxPunktZahl = 0

        for (spieler in spielerManager.spieler) {

            val punktZahl = ermittlePunktzahl(spieler)

            if (punktZahl > maxPunktZahl) {
                gewinner = spieler
                maxPunktZahl = punktZahl
            }
        }

        return gewinner!!
    }

    fun ermittlePunktzahl(spieler: Spieler): Int {

        var punkte = 0

        for (kaestchen in spielfeld!!.kaestchenListe)
            if (kaestchen.besitzer == spieler)
                punkte++

        return punkte
    }
}