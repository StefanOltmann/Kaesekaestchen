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

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.content.ContextCompat
import de.stefan_oltmann.kaesekaestchen.R
import de.stefan_oltmann.kaesekaestchen.model.Spielfeld
import de.stefan_oltmann.kaesekaestchen.model.Strich
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import kotlin.concurrent.withLock

/**
 * Diese Klasse zeichnet das Spielfeld und nimmt Interaktionen des Benutzers
 * entgegen.
 *
 * @author Stefan Oltmann
 */
class SpielfeldView(context: Context?, attrs: AttributeSet?) : View(context, attrs), OnTouchListener {

    companion object {
        var KAESTCHEN_SEITENLAENGE = 50
        var PADDING = 5
    }

    private var spielfeld: Spielfeld? = null

    private var lock: Lock? = null
    private var condition: Condition? = null

    /**
     * Über die letzte Eingabe wird in Erfahrung gebracht, was der Nutzer
     * möchte. Der Abruf dieses Wertes ist sozusagen im Spiel-Ablauf blocking.
     */
    @Volatile
    var letzteEingabe: Strich? = null
        private set

    fun init(spielfeld: Spielfeld?, lock: Lock, condition: Condition) {
        this.spielfeld = spielfeld
        this.lock = lock
        this.condition = condition
        setOnTouchListener(this)
    }

    fun resetLetzteEingabe() {
        letzteEingabe = null
    }

    /**
     * Wird die Bildschirmauflösung verändert oder initial bekanntgegen, wird
     * diese Methode aufgerufen. Wir benutzen das um zu ermitteln, wie groß ein
     * Kästchen in Abhängigkeit von der Auflösung des Displays sein muss.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        if (spielfeld == null)
            return

        val maxBreite = (w - PADDING * 2) / spielfeld!!.breiteInKaestchen
        val maxHoehe = (h - PADDING * 2) / spielfeld!!.hoeheInKaestchen

        KAESTCHEN_SEITENLAENGE = kotlin.math.min(maxBreite, maxHoehe)
    }

    override fun onDraw(canvas: Canvas) {

        canvas.drawColor(ContextCompat.getColor(context,
            R.color.hintergrund_farbe
        ))

        /*
         * Wurde das Spielfeld noch nicht initalisiert, dieses nicht zeichnen.
         * Ansonsten würde das zu einer NullPointer-Exception führen. Dies wird
         * auch benötigt, um korrekt im GUI-Editor dargestellt zu werden.
         */
        if (spielfeld == null) {

            canvas.drawRect(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                Paint()
            )

            return
        }

        for (kaestchen in spielfeld!!.kaestchenListe)
            kaestchen.onDraw(canvas)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {

        /*
         * Es gibt verschiedene MotionEvents, aber hier interessiert uns nur das
         * tatsächliche Drücken auf den Bildschirm.
         */
        if (event.action != MotionEvent.ACTION_DOWN)
            return true

        if (letzteEingabe != null)
            return true

        val errechnetRasterX = event.x.toInt() / KAESTCHEN_SEITENLAENGE
        val errechnetRasterY = event.y.toInt() / KAESTCHEN_SEITENLAENGE
        val kaestchen = spielfeld!!.getKaestchen(errechnetRasterX, errechnetRasterY)

        /*
         * Wenn sich an der berührten Position kein Kästchen befindet oder
         * dieses schon einen Besitzer hat, die Eingabe ignorieren.
         */
        if (kaestchen.besitzer != null)
            return true

        val strich = kaestchen.ermittleStrich(event.x.toInt(), event.y.toInt()) ?: return true

        /*
         * Konnte kein Strich ermittelt werden, hat der Benutzer wahrscheinlich
         * die Mitte des Kästchens getroffen. Es ist jedenfalls nicht klar,
         * welchen Strich er gemeint hat. Deshalb wird die Eingabe abgebrochen.
         */

        /*
         * An dieser Stelle hat der Benutzer seine Eingabe erfolgreich getätigt.
         * Wir schreiben seine Eingabe in eine Zwischenvariable die zur
         * Kommunikation mit dem Gameloop-Thread verwendet wird und wecken
         * diesen via "notifyAll" wieder auf. Der Gameloop-Thread wurde zuvor
         * mit "await()" auf der Condition pausiert.
         */
        letzteEingabe = strich

        lock!!.withLock {
            condition!!.signalAll()
        }

        return true
    }

    fun aktualisiereAnzeige() {
        postInvalidate() // View zwingen, neu zu zeichnen
    }
}