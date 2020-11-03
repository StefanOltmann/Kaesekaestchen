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
package de.stefan_oltmann.kaesekaestchen.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import de.stefan_oltmann.kaesekaestchen.R
import de.stefan_oltmann.kaesekaestchen.controller.GameLoop
import de.stefan_oltmann.kaesekaestchen.model.Kaestchen
import de.stefan_oltmann.kaesekaestchen.model.Spieler
import de.stefan_oltmann.kaesekaestchen.model.Strich

/**
 * Diese Klasse zeichnet das Spielfeld und nimmt Interaktionen des Benutzers
 * entgegen.
 *
 * @author Stefan Oltmann
 */
class SpielfeldView(context: Context?, attrs: AttributeSet?) : View(context, attrs), OnTouchListener {

    companion object {
        var PADDING_PX = 10
    }

    private lateinit var gameLoop: GameLoop

    /*
     * Seitenlaenge eines Kästchens in Pixel
     */
    private var kaestchenSeitenlaengePixel = 50

    private val defaultRahmenColor by lazy {
        ContextCompat.getColor(context!!, R.color.kaestchen_rahmen_farbe)
    }

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

    fun init(gameLoop: GameLoop) {

        this.gameLoop = gameLoop

        setOnTouchListener(this)
    }

    /**
     * Wird die Bildschirmauflösung verändert oder initial bekanntgegen, wird
     * diese Methode aufgerufen. Wir benutzen das um zu ermitteln, wie groß ein
     * Kästchen in Abhängigkeit von der Auflösung des Displays sein muss.
     */
    override fun onSizeChanged(breitePixel: Int, hoehePixel: Int, oldw: Int, oldh: Int) {

        val maxBreitePixel = (breitePixel - PADDING_PX * 2) / gameLoop.spielfeld.breiteInKaestchen
        val maxHoehePixel = (hoehePixel - PADDING_PX * 2) / gameLoop.spielfeld.hoeheInKaestchen

        kaestchenSeitenlaengePixel = kotlin.math.min(maxBreitePixel, maxHoehePixel)
    }

    override fun onDraw(canvas: Canvas) {

        for (kaestchen in gameLoop.spielfeld.kaestchenListeUnmodifiable)
            drawKaestchen(kaestchen, canvas)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {

        /*
         * Es gibt verschiedene MotionEvents, aber hier interessiert uns nur das
         * tatsächliche Drücken auf den Bildschirm.
         */
        if (event.action != MotionEvent.ACTION_DOWN)
            return true

        if (gameLoop.letzteEingabe != null)
            return true

        val errechnetRasterX = event.x.toInt() / kaestchenSeitenlaengePixel
        val errechnetRasterY = event.y.toInt() / kaestchenSeitenlaengePixel

        /*
         * Wenn der Anwender irgendwo außerhalb des Spielfelds drückt soll
         * dies einfach ignoriert werden und nicht zu einem Fehler führen.
         */
        if (!gameLoop.spielfeld.isImRaster(errechnetRasterX, errechnetRasterY))
            return true

        val kaestchen = gameLoop.spielfeld.getKaestchen(errechnetRasterX, errechnetRasterY)

        /*
         * Wenn sich an der berührten Position kein Kästchen befindet oder
         * dieses schon einen Besitzer hat, die Eingabe ignorieren.
         */
        if (kaestchen.besitzer != null)
            return true

        val strich = ermittleStrich(kaestchen, event.x.toInt(), event.y.toInt())

        /*
         * Konnte kein Strich ermittelt werden, hat der Benutzer wahrscheinlich
         * die Mitte des Kästchens getroffen. Es ist jedenfalls nicht klar,
         * welchen Strich er gemeint hat. Deshalb wird die Eingabe abgebrochen.
         */
        if (strich == null)
            return true

        gameLoop.behandleNutzerEingabe(strich)

        return true
    }

    private fun calcPixelX(kaestchen: Kaestchen) =
        kaestchen.rasterX * kaestchenSeitenlaengePixel + PADDING_PX

    private fun calcPixelY(kaestchen: Kaestchen) =
        kaestchen.rasterY * kaestchenSeitenlaengePixel + PADDING_PX

    private fun calcRectStrichOben(kaestchen: Kaestchen): Rect? =
        if (kaestchen.strichOben == null) null else Rect(
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel / 4,
            calcPixelY(kaestchen) - kaestchenSeitenlaengePixel / 4,
            calcPixelX(kaestchen) + (kaestchenSeitenlaengePixel * 0.75).toInt(),
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel / 4
        )

    private fun calcRectStrichUnten(kaestchen: Kaestchen): Rect? =
        if (kaestchen.strichUnten == null) null else Rect(
            calcPixelX(kaestchen)  + kaestchenSeitenlaengePixel / 4,
            calcPixelY(kaestchen) + (kaestchenSeitenlaengePixel * 0.75).toInt(),
            calcPixelX(kaestchen)  + (kaestchenSeitenlaengePixel * 0.75).toInt(),
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel + kaestchenSeitenlaengePixel / 4
        )

    private fun calcRectStrichLinks(kaestchen: Kaestchen): Rect? =
        if (kaestchen.strichLinks == null) null else Rect(
            calcPixelX(kaestchen)  - kaestchenSeitenlaengePixel / 4,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel / 4,
            calcPixelX(kaestchen)  + kaestchenSeitenlaengePixel / 4,
            calcPixelY(kaestchen) + (kaestchenSeitenlaengePixel * 0.75).toInt()
        )

    private fun calcRectStrichRechts(kaestchen: Kaestchen): Rect? =
        if (kaestchen.strichRechts == null) null else Rect(
            calcPixelX(kaestchen)  + (kaestchenSeitenlaengePixel * 0.75).toInt(),
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel / 4,
            calcPixelX(kaestchen)  + kaestchenSeitenlaengePixel + kaestchenSeitenlaengePixel / 4,
            calcPixelY(kaestchen) + (kaestchenSeitenlaengePixel * 0.75).toInt()
        )

    /**
     * Diese Methode bestimmt, auf welchen Strich des Kästchen gedrückt wurde.
     */
    private fun ermittleStrich(kaestchen: Kaestchen, pixelX: Int, pixelY: Int): Strich? {

        calcRectStrichOben(kaestchen)?.let {
            if (it.contains(pixelX, pixelY))
                return kaestchen.strichOben
        }

        calcRectStrichUnten(kaestchen)?.let {
            if (it.contains(pixelX, pixelY))
                return kaestchen.strichUnten
        }

        calcRectStrichLinks(kaestchen)?.let {
            if (it.contains(pixelX, pixelY))
                return kaestchen.strichLinks
        }

        calcRectStrichRechts(kaestchen)?.let {
            if (it.contains(pixelX, pixelY))
                return kaestchen.strichRechts
        }

        return null
    }

    private fun getFarbeFuerSpieler(spieler: Spieler) =
        if (spieler == Spieler.KAESE)
            ContextCompat.getColor(context, R.color.spieler_kaese_farbe)
        else
            ContextCompat.getColor(context, R.color.spieler_maus_farbe)

    private fun drawKaestchen(kaestchen: Kaestchen, canvas: Canvas) {

        val pixelX = calcPixelX(kaestchen)
        val pixelY = calcPixelY(kaestchen)

        kaestchen.besitzer?.let {

            fuellungPaint.color = getFarbeFuerSpieler(it)

            val symbol : Drawable =
                if (it == Spieler.KAESE)
                    AppCompatResources.getDrawable(context!!, R.drawable.ic_spieler_symbol_kaese)!!
                else
                    AppCompatResources.getDrawable(context!!, R.drawable.ic_spieler_symbol_maus)!!

            symbol.setBounds(0, 0, kaestchenSeitenlaengePixel, kaestchenSeitenlaengePixel)
            canvas.translate(pixelX.toFloat(), pixelY.toFloat())
            symbol.draw(canvas)
            canvas.translate(-pixelX.toFloat(), -pixelY.toFloat())
        }

        if (kaestchen.strichOben == null) {

            rahmenPaint.color = Color.BLACK

            canvas.drawLine(
                pixelX.toFloat(),
                pixelY.toFloat(),
                pixelX + kaestchenSeitenlaengePixel.toFloat(),
                pixelY.toFloat(),
                rahmenPaint
            )
        }

        if (kaestchen.strichUnten != null && kaestchen.strichUnten!!.besitzer != null)
            rahmenPaint.color = getFarbeFuerSpieler(kaestchen.strichUnten!!.besitzer!!)
        else if (kaestchen.strichUnten != null)
            rahmenPaint.color = defaultRahmenColor
        else
            rahmenPaint.color = Color.BLACK

        canvas.drawLine(
            pixelX.toFloat(),
            pixelY + kaestchenSeitenlaengePixel.toFloat(),
            pixelX + kaestchenSeitenlaengePixel.toFloat(),
            pixelY + kaestchenSeitenlaengePixel.toFloat(),
            rahmenPaint
        )

        if (kaestchen.strichLinks == null) {

            rahmenPaint.color = Color.BLACK

            canvas.drawLine(
                pixelX.toFloat(),
                pixelY.toFloat(),
                pixelX.toFloat(),
                pixelY + kaestchenSeitenlaengePixel.toFloat(),
                rahmenPaint
            )
        }

        if (kaestchen.strichRechts != null && kaestchen.strichRechts!!.besitzer != null)
            rahmenPaint.color = getFarbeFuerSpieler(kaestchen.strichRechts!!.besitzer!!)
        else if (kaestchen.strichRechts != null)
            rahmenPaint.color = defaultRahmenColor
        else
            rahmenPaint.color = Color.BLACK

        canvas.drawLine(
            pixelX + kaestchenSeitenlaengePixel.toFloat(),
            pixelY.toFloat(),
            pixelX + kaestchenSeitenlaengePixel.toFloat(),
            pixelY + kaestchenSeitenlaengePixel.toFloat(),
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
            pixelX + kaestchenSeitenlaengePixel - 1.toFloat(),
            pixelY - 1.toFloat(),
            pixelX + kaestchenSeitenlaengePixel + 1.toFloat(),
            pixelY + 1.toFloat(),
            rahmenPaint
        )

        canvas.drawRect(
            pixelX - 1.toFloat(),
            pixelY + kaestchenSeitenlaengePixel - 1.toFloat(),
            pixelX + 1.toFloat(),
            pixelY + kaestchenSeitenlaengePixel + 1.toFloat(),
            rahmenPaint
        )

        canvas.drawRect(
            pixelX + kaestchenSeitenlaengePixel - 1.toFloat(),
            pixelY + kaestchenSeitenlaengePixel - 1.toFloat(),
            pixelX + kaestchenSeitenlaengePixel + 1.toFloat(),
            pixelY + kaestchenSeitenlaengePixel + 1.toFloat(),
            rahmenPaint
        )
    }

    fun aktualisiereAnzeige() {
        postInvalidate() // View zwingen, neu zu zeichnen
    }
}