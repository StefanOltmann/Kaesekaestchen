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
package de.stefan_oltmann.kaesekaestchen.ui.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import de.stefan_oltmann.kaesekaestchen.R
import de.stefan_oltmann.kaesekaestchen.controller.SpielLogik
import de.stefan_oltmann.kaesekaestchen.model.Kaestchen
import de.stefan_oltmann.kaesekaestchen.model.Spieler
import de.stefan_oltmann.kaesekaestchen.model.Strich
import kotlin.math.roundToInt

/**
 * Diese Klasse zeichnet das Spielfeld und nimmt Interaktionen des Benutzers
 * entgegen.
 *
 * @author Stefan Oltmann
 */
class SpielfeldView(context: Context?, attrs: AttributeSet?) : View(context, attrs), OnTouchListener {

    companion object {
        var PADDING_PX = 10f
    }

    private lateinit var spielLogik: SpielLogik

    /*
     * Seitenlaenge eines Kästchens in Pixel
     */
    private var kaestchenSeitenlaengePixel = 50.0f

    /*
     * Ein Offset, um das Spielfeld zentriert zu haben
     */
    private var offsetPixelX = 0.0f
    private var offsetPixelY = 0.0f

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

    fun setGameLoop(spielLogik: SpielLogik) {

        this.spielLogik = spielLogik

        setOnTouchListener(this)
    }

    /**
     * Wird die Viewgröße verändert oder initial bekanntgegen, wird
     * diese Methode aufgerufen. Wir benutzen das um zu ermitteln, wie groß ein
     * Kästchen in Abhängigkeit von der Größe des Displays sein muss.
     */
    override fun onSizeChanged(breitePixel: Int, hoehePixel: Int, oldw: Int, oldh: Int) {

        val breitePixelMitPadding = breitePixel.toFloat() - PADDING_PX * 2
        val hoehePixelMitPadding = hoehePixel.toFloat() - PADDING_PX * 2

        val maxBreitePixel : Float = breitePixelMitPadding / spielLogik.spielfeld.breiteInKaestchen
        val maxHoehePixel : Float = hoehePixelMitPadding / spielLogik.spielfeld.hoeheInKaestchen

        kaestchenSeitenlaengePixel = kotlin.math.min(maxBreitePixel, maxHoehePixel)

        offsetPixelX = (breitePixelMitPadding - spielLogik.spielfeld.breiteInKaestchen * kaestchenSeitenlaengePixel) / 2.0f
        offsetPixelY = (hoehePixelMitPadding - spielLogik.spielfeld.hoeheInKaestchen * kaestchenSeitenlaengePixel) / 2.0f
    }

    override fun onDraw(canvas: Canvas) {

        for (kaestchen in spielLogik.spielfeld.kaestchenListe)
            drawKaestchen(kaestchen, canvas)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {

        /*
         * Es gibt verschiedene MotionEvents, aber hier interessiert uns nur das
         * tatsächliche Drücken auf den Bildschirm.
         */
        if (event.action != MotionEvent.ACTION_DOWN)
            return true

        /*
         * Für die Bestimmung des Kästchens muss hier bewusst durch toInt()
         * die Nachkomma-Stelle abgeschnitten werden. Eine Rundung über
         * roundToInt() würde das falsche Kästchen ausgeben.
         */
        val errechnetRasterX = (event.x / kaestchenSeitenlaengePixel).toInt()
        val errechnetRasterY = (event.y / kaestchenSeitenlaengePixel).toInt()

        /*
         * Wenn der Anwender irgendwo außerhalb des Spielfelds drückt soll
         * dies einfach ignoriert werden und nicht zu einem Fehler führen.
         */
        if (!spielLogik.spielfeld.isImRaster(errechnetRasterX, errechnetRasterY))
            return true

        val kaestchen = spielLogik.spielfeld.getKaestchen(errechnetRasterX, errechnetRasterY)

        /*
         * Wenn sich an der berührten Position kein Kästchen befindet oder
         * dieses schon einen Besitzer hat, die Eingabe ignorieren.
         */
        if (kaestchen.besitzer != null)
            return true

        val strich = ermittleStrichAnPosition(kaestchen, event.x, event.y)

        /*
         * Konnte kein Strich ermittelt werden, hat der Benutzer wahrscheinlich
         * die Mitte des Kästchens getroffen. Es ist jedenfalls nicht klar,
         * welchen Strich er gemeint hat. Deshalb wird die Eingabe abgebrochen.
         */
        @Suppress("FoldInitializerAndIfToElvis")
        if (strich == null)
            return true

        spielLogik.behandleSpielerEingabe(strich)

        return true
    }

    private fun calcPixelX(kaestchen: Kaestchen) =
        kaestchen.rasterX * kaestchenSeitenlaengePixel + PADDING_PX + offsetPixelX

    private fun calcPixelY(kaestchen: Kaestchen) =
        kaestchen.rasterY * kaestchenSeitenlaengePixel + PADDING_PX + offsetPixelY

    private fun calcRectStrichOben(kaestchen: Kaestchen): RectF? =
        if (kaestchen.strichOben == null) null else RectF(
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * 0.25f,
            calcPixelY(kaestchen) - kaestchenSeitenlaengePixel * 0.25f,
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * 0.75f,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * 0.25f)

    private fun calcRectStrichUnten(kaestchen: Kaestchen): RectF? =
        if (kaestchen.strichUnten == null) null else RectF(
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * 0.25f,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * 0.75f,
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * 0.75f,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * 1.25f)

    private fun calcRectStrichLinks(kaestchen: Kaestchen): RectF? =
        if (kaestchen.strichLinks == null) null else RectF(
            calcPixelX(kaestchen) - kaestchenSeitenlaengePixel * 0.25f,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * 0.25f,
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * 0.25f,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * 0.75f)

    private fun calcRectStrichRechts(kaestchen: Kaestchen): RectF? =
        if (kaestchen.strichRechts == null) null else RectF(
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * 0.75f,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * 0.25f,
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * 1.25f,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * 0.75f)

    /**
     * Diese Methode bestimmt, auf welchen Strich des Kästchen gedrückt wurde.
     */
    private fun ermittleStrichAnPosition(kaestchen: Kaestchen, pixelX: Float, pixelY: Float): Strich? {

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

            val symbol: Drawable =
                if (it == Spieler.KAESE)
                    AppCompatResources.getDrawable(context!!, R.drawable.ic_spieler_symbol_kaese)!!
                else
                    AppCompatResources.getDrawable(context!!, R.drawable.ic_spieler_symbol_maus)!!

            symbol.setBounds(0, 0, kaestchenSeitenlaengePixel.roundToInt(), kaestchenSeitenlaengePixel.roundToInt())
            canvas.translate(pixelX, pixelY)
            symbol.draw(canvas)
            canvas.translate(-pixelX, -pixelY)
        }

        if (kaestchen.strichOben == null) {

            rahmenPaint.color = Color.BLACK

            canvas.drawLine(
                pixelX,
                pixelY,
                pixelX + kaestchenSeitenlaengePixel,
                pixelY,
                rahmenPaint
            )
        }

        rahmenPaint.color = ermittleStrichFarbe(kaestchen.strichUnten)

        canvas.drawLine(
            pixelX,
            pixelY + kaestchenSeitenlaengePixel,
            pixelX + kaestchenSeitenlaengePixel,
            pixelY + kaestchenSeitenlaengePixel,
            rahmenPaint
        )

        if (kaestchen.strichLinks == null) {

            rahmenPaint.color = Color.BLACK

            canvas.drawLine(
                pixelX,
                pixelY,
                pixelX,
                pixelY + kaestchenSeitenlaengePixel,
                rahmenPaint
            )
        }

        rahmenPaint.color = ermittleStrichFarbe(kaestchen.strichRechts)

        canvas.drawLine(
            pixelX + kaestchenSeitenlaengePixel,
            pixelY,
            pixelX + kaestchenSeitenlaengePixel,
            pixelY + kaestchenSeitenlaengePixel,
            rahmenPaint
        )

        /* Eckpunkte zeichnen */

        rahmenPaint.color = Color.BLACK

        canvas.drawRect(
            pixelX - 1.0f,
            pixelY - 1.0f,
            pixelX + 1.0f,
            pixelY + 1.0f,
            rahmenPaint
        )

        canvas.drawRect(
            pixelX + kaestchenSeitenlaengePixel - 1.0f,
            pixelY - 1.0f,
            pixelX + kaestchenSeitenlaengePixel + 1.0f,
            pixelY + 1.0f,
            rahmenPaint
        )

        canvas.drawRect(
            pixelX - 1.0f,
            pixelY + kaestchenSeitenlaengePixel - 1.0f,
            pixelX + 1.0f,
            pixelY + kaestchenSeitenlaengePixel + 1.0f,
            rahmenPaint
        )

        canvas.drawRect(
            pixelX + kaestchenSeitenlaengePixel - 1.0f,
            pixelY + kaestchenSeitenlaengePixel - 1.0f,
            pixelX + kaestchenSeitenlaengePixel + 1.0f,
            pixelY + kaestchenSeitenlaengePixel + 1.0f,
            rahmenPaint
        )
    }

    private fun ermittleStrichFarbe(strich: Strich?) : Int {

        return if (strich != null && strich == spielLogik.spielfeld.zuletztGesetzterStrich)
            Color.CYAN
        else if (strich?.besitzer != null)
            getFarbeFuerSpieler(strich.besitzer!!)
        else if (strich != null)
            defaultRahmenColor
        else
            Color.BLACK
    }

    fun aktualisiereAnzeige() {
        postInvalidate() // View zwingen, neu zu zeichnen
    }
}