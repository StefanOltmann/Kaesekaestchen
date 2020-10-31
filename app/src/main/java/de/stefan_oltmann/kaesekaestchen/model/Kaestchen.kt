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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import de.stefan_oltmann.kaesekaestchen.ui.SpielfeldView

/**
 * Ein Kästchen auf dem Spielfeld.
 *
 * @author Stefan Oltmann
 */
data class Kaestchen(val rasterX: Int,
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

    private val rahmenPaint = Paint()
    private val fuellungPaint = Paint()

    /**
     * Konstruktor zum Erstellen des Kästchen. Es muss die Position/ID des
     * Kästchen angegeben werden.
     */
    init {
        rahmenPaint.style = Paint.Style.STROKE
        rahmenPaint.strokeWidth = 5f
    }

    private val pixelX: Int
        get() = rasterX * SpielfeldView.KAESTCHEN_SEITENLAENGE + SpielfeldView.PADDING

    private val pixelY: Int
        get() = rasterY * SpielfeldView.KAESTCHEN_SEITENLAENGE + SpielfeldView.PADDING

    val stricheOhneBesitzer: List<Strich>
        get() {

            val striche: MutableList<Strich> = mutableListOf()

            if (strichOben != null && strichOben!!.besitzer == null) striche.add(strichOben!!)
            if (strichUnten != null && strichUnten!!.besitzer == null) striche.add(strichUnten!!)
            if (strichLinks != null && strichLinks!!.besitzer == null) striche.add(strichLinks!!)
            if (strichRechts != null && strichRechts!!.besitzer == null) striche.add(strichRechts!!)

            return striche
        }

    val isAlleStricheHabenBesitzer: Boolean
        get() = stricheOhneBesitzer.isEmpty()

    private val rectStrichOben: Rect?
        get() = if (strichOben == null) null else Rect(
            pixelX + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4,
            pixelY - SpielfeldView.KAESTCHEN_SEITENLAENGE / 4,
            pixelX + (SpielfeldView.KAESTCHEN_SEITENLAENGE * 0.75).toInt(),
            pixelY + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4
        )

    private val rectStrichUnten: Rect?
        get() = if (strichUnten == null) null else Rect(
            pixelX + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4,
            pixelY + (SpielfeldView.KAESTCHEN_SEITENLAENGE * 0.75).toInt(),
            pixelX + (SpielfeldView.KAESTCHEN_SEITENLAENGE * 0.75).toInt(),
            pixelY + SpielfeldView.KAESTCHEN_SEITENLAENGE + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4
        )

    private val rectStrichLinks: Rect?
        get() = if (strichLinks == null) null else Rect(
            pixelX - SpielfeldView.KAESTCHEN_SEITENLAENGE / 4,
            pixelY + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4,
            pixelX + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4,
            pixelY + (SpielfeldView.KAESTCHEN_SEITENLAENGE * 0.75).toInt()
        )

    private val rectStrichRechts: Rect?
        get() = if (strichRechts == null) null else Rect(
            pixelX + (SpielfeldView.KAESTCHEN_SEITENLAENGE * 0.75).toInt(),
            pixelY + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4,
            pixelX + SpielfeldView.KAESTCHEN_SEITENLAENGE + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4,
            pixelY + (SpielfeldView.KAESTCHEN_SEITENLAENGE * 0.75).toInt()
        )

    /**
     * Diese Methode bestimmt, auf welchen Strich des Kästchen gedrückt wurde.
     */
    fun ermittleStrich(pixelX: Int, pixelY: Int): Strich? {

        if (rectStrichOben != null && rectStrichOben!!.contains(pixelX, pixelY))
            return strichOben

        if (rectStrichUnten != null && rectStrichUnten!!.contains(pixelX, pixelY))
            return strichUnten

        if (rectStrichLinks != null && rectStrichLinks!!.contains(pixelX, pixelY))
            return strichLinks

        if (rectStrichRechts != null && rectStrichRechts!!.contains(pixelX, pixelY))
            return strichRechts

        return null
    }

    fun onDraw(canvas: Canvas) {

        if (besitzer != null) {

            fuellungPaint.color = besitzer!!.farbe

            val symbol = besitzer!!.symbol

            symbol.setBounds(0, 0, SpielfeldView.KAESTCHEN_SEITENLAENGE, SpielfeldView.KAESTCHEN_SEITENLAENGE)
            canvas.translate(pixelX.toFloat(), pixelY.toFloat())
            symbol.draw(canvas)
            canvas.translate(-pixelX.toFloat(), -pixelY.toFloat())
        }

        if (strichOben == null) {

            rahmenPaint.color = Color.BLACK

            canvas.drawLine(
                pixelX.toFloat(),
                pixelY.toFloat(),
                pixelX + SpielfeldView.KAESTCHEN_SEITENLAENGE.toFloat(),
                pixelY.toFloat(),
                rahmenPaint
            )
        }

        if (strichUnten != null && strichUnten!!.besitzer != null)
            rahmenPaint.color = strichUnten!!.besitzer!!.farbe
        else if (strichUnten != null)
            rahmenPaint.color = Color.LTGRAY
        else
            rahmenPaint.color = Color.BLACK

        canvas.drawLine(
            pixelX.toFloat(),
            pixelY + SpielfeldView.KAESTCHEN_SEITENLAENGE.toFloat(),
            pixelX + SpielfeldView.KAESTCHEN_SEITENLAENGE.toFloat(),
            pixelY + SpielfeldView.KAESTCHEN_SEITENLAENGE.toFloat(),
            rahmenPaint
        )

        if (strichLinks == null) {

            rahmenPaint.color = Color.BLACK

            canvas.drawLine(
                pixelX.toFloat(),
                pixelY.toFloat(),
                pixelX.toFloat(),
                pixelY + SpielfeldView.KAESTCHEN_SEITENLAENGE.toFloat(),
                rahmenPaint
            )
        }

        if (strichRechts != null && strichRechts!!.besitzer != null)
            rahmenPaint.color = strichRechts!!.besitzer!!.farbe
        else if (strichRechts != null)
            rahmenPaint.color = Color.LTGRAY
        else
            rahmenPaint.color = Color.BLACK

        canvas.drawLine(
            pixelX + SpielfeldView.KAESTCHEN_SEITENLAENGE.toFloat(),
            pixelY.toFloat(),
            pixelX + SpielfeldView.KAESTCHEN_SEITENLAENGE.toFloat(),
            pixelY + SpielfeldView.KAESTCHEN_SEITENLAENGE.toFloat(),
            rahmenPaint
        )

        /* Eckpunkte zeichnen */
        rahmenPaint.color = Color.BLACK

        canvas.drawRect(
            pixelX - 1.toFloat(),
            pixelY - 1.toFloat(),
            pixelX + 1.toFloat(),
            pixelY + 1.toFloat(),
            rahmenPaint
        )

        canvas.drawRect(
            pixelX + SpielfeldView.KAESTCHEN_SEITENLAENGE - 1.toFloat(),
            pixelY - 1.toFloat(),
            pixelX + SpielfeldView.KAESTCHEN_SEITENLAENGE + 1.toFloat(),
            pixelY + 1.toFloat(),
            rahmenPaint
        )

        canvas.drawRect(
            pixelX - 1.toFloat(),
            pixelY + SpielfeldView.KAESTCHEN_SEITENLAENGE - 1.toFloat(),
            pixelX + 1.toFloat(),
            pixelY + SpielfeldView.KAESTCHEN_SEITENLAENGE + 1.toFloat(),
            rahmenPaint
        )

        canvas.drawRect(
            pixelX + SpielfeldView.KAESTCHEN_SEITENLAENGE - 1.toFloat(),
            pixelY + SpielfeldView.KAESTCHEN_SEITENLAENGE - 1.toFloat(),
            pixelX + SpielfeldView.KAESTCHEN_SEITENLAENGE + 1.toFloat(),
            pixelY + SpielfeldView.KAESTCHEN_SEITENLAENGE + 1.toFloat(),
            rahmenPaint
        )
    }
}