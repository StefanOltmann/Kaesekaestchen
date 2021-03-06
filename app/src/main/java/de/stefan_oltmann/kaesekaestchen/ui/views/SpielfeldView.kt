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
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
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
 */
class SpielfeldView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs), OnTouchListener {

    private lateinit var spielLogik: SpielLogik

    /*
     * Seitenlaenge eines Kästchens in Pixel
     */
    private var kaestchenSeitenlaengePixel = INITIAL_KAESTCHEN_SEITENLAENGE_PIXEL

    /*
     * Ein Offset, um das Spielfeld zentriert zu haben
     */
    private var offsetPixelX = 0.0f
    private var offsetPixelY = 0.0f

    private val rahmenFarbe by lazy {
        ContextCompat.getColor(context!!, R.color.rahmen_farbe)
    }

    private val strichOhneBesitzerFarbe by lazy {
        ContextCompat.getColor(context!!, R.color.strich_ohne_besitzer_farbe)
    }

    private val zuletztGesetzterStrichFarbe by lazy {
        ContextCompat.getColor(context!!, R.color.zuletzt_gesetzter_strich_farbe)
    }

    private val rahmenPaint = Paint()

    /**
     * Konstruktor zum Erstellen des Kästchen. Es muss die Position/ID des
     * Kästchen angegeben werden.
     */
    init {
        rahmenPaint.style = Paint.Style.STROKE
        rahmenPaint.strokeWidth = STRICH_DICKE_PX
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

        val maxBreitePixel: Float = breitePixelMitPadding / spielLogik.spielfeld.breiteInKaestchen
        val maxHoehePixel: Float = hoehePixelMitPadding / spielLogik.spielfeld.hoeheInKaestchen

        kaestchenSeitenlaengePixel = kotlin.math.min(maxBreitePixel, maxHoehePixel)

        offsetPixelX =
            (breitePixelMitPadding - spielLogik.spielfeld.breiteInKaestchen * kaestchenSeitenlaengePixel) / 2.0f
        offsetPixelY =
            (hoehePixelMitPadding - spielLogik.spielfeld.hoeheInKaestchen * kaestchenSeitenlaengePixel) / 2.0f
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

        val eventX = event.x - PADDING_PX - offsetPixelX
        val eventY = event.y - PADDING_PX - offsetPixelY

        val errechnetRasterX = (eventX / kaestchenSeitenlaengePixel).toInt()
        val errechnetRasterY = (eventY / kaestchenSeitenlaengePixel).toInt()

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
        if (kaestchen.strichOben == null)
            null
        else RectF(
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * MULTI_0_25,
            calcPixelY(kaestchen) - kaestchenSeitenlaengePixel * MULTI_0_25,
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * MULTI_0_75,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * MULTI_0_25
        )

    private fun calcRectStrichUnten(kaestchen: Kaestchen): RectF? =
        if (kaestchen.strichUnten == null)
            null
        else RectF(
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * MULTI_0_25,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * MULTI_0_75,
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * MULTI_0_75,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * MULTI_1_25
        )

    private fun calcRectStrichLinks(kaestchen: Kaestchen): RectF? =
        if (kaestchen.strichLinks == null)
            null
        else RectF(
            calcPixelX(kaestchen) - kaestchenSeitenlaengePixel * MULTI_0_25,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * MULTI_0_25,
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * MULTI_0_25,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * MULTI_0_75
        )

    private fun calcRectStrichRechts(kaestchen: Kaestchen): RectF? =
        if (kaestchen.strichRechts == null)
            null
        else RectF(
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * MULTI_0_75,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * MULTI_0_25,
            calcPixelX(kaestchen) + kaestchenSeitenlaengePixel * MULTI_1_25,
            calcPixelY(kaestchen) + kaestchenSeitenlaengePixel * MULTI_0_75
        )

    /**
     * Diese Methode bestimmt, auf welchen Strich des Kästchen gedrückt wurde.
     */
    private fun ermittleStrichAnPosition(
        kaestchen: Kaestchen,
        pixelX: Float,
        pixelY: Float
    ): Strich? {

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

    private fun drawKaestchen(kaestchen: Kaestchen, canvas: Canvas) {

        val pixelX = calcPixelX(kaestchen)
        val pixelY = calcPixelY(kaestchen)

        /* Symbol einzeichnen */

        kaestchen.besitzer?.let {

            val symbol: Drawable =
                if (it == Spieler.KAESE)
                    AppCompatResources.getDrawable(context!!, R.drawable.ic_spieler_symbol_kaese)!!
                else
                    AppCompatResources.getDrawable(context!!, R.drawable.ic_spieler_symbol_maus)!!

            symbol.setBounds(
                0,
                0,
                kaestchenSeitenlaengePixel.roundToInt() - SYMBOL_PADDING_PX * 2,
                kaestchenSeitenlaengePixel.roundToInt() - SYMBOL_PADDING_PX * 2
            )

            val pixelXmitPadding = pixelX + SYMBOL_PADDING_PX
            val pixelYmitPadding = pixelY + SYMBOL_PADDING_PX

            canvas.translate(pixelXmitPadding, pixelYmitPadding)
            symbol.draw(canvas)
            canvas.translate(-pixelXmitPadding, -pixelYmitPadding)
        }

        /* Striche zeichnen */

        if (kaestchen.strichOben == null) {

            rahmenPaint.color = rahmenFarbe

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

            rahmenPaint.color = rahmenFarbe

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

        /*
         * Eckpunkte zeichnen
         *
         * Bewusst nicht drawCircle(), weil es in eckig einfach besser aussieht.
         */

        rahmenPaint.color = rahmenFarbe

        canvas.drawRect(
            pixelX - ECKPUNKT_RADIUS_PX,
            pixelY - ECKPUNKT_RADIUS_PX,
            pixelX + ECKPUNKT_RADIUS_PX,
            pixelY + ECKPUNKT_RADIUS_PX,
            rahmenPaint
        )

        canvas.drawRect(
            pixelX + kaestchenSeitenlaengePixel - ECKPUNKT_RADIUS_PX,
            pixelY - ECKPUNKT_RADIUS_PX,
            pixelX + kaestchenSeitenlaengePixel + ECKPUNKT_RADIUS_PX,
            pixelY + ECKPUNKT_RADIUS_PX,
            rahmenPaint
        )

        canvas.drawRect(
            pixelX - ECKPUNKT_RADIUS_PX,
            pixelY + kaestchenSeitenlaengePixel - ECKPUNKT_RADIUS_PX,
            pixelX + ECKPUNKT_RADIUS_PX,
            pixelY + kaestchenSeitenlaengePixel + ECKPUNKT_RADIUS_PX,
            rahmenPaint
        )

        canvas.drawRect(
            pixelX + kaestchenSeitenlaengePixel - ECKPUNKT_RADIUS_PX,
            pixelY + kaestchenSeitenlaengePixel - ECKPUNKT_RADIUS_PX,
            pixelX + kaestchenSeitenlaengePixel + ECKPUNKT_RADIUS_PX,
            pixelY + kaestchenSeitenlaengePixel + ECKPUNKT_RADIUS_PX,
            rahmenPaint
        )
    }

    private fun ermittleStrichFarbe(strich: Strich?): Int {

        return if (strich != null && strich == spielLogik.spielfeld.zuletztGesetzterStrich)
            zuletztGesetzterStrichFarbe
        else if (strich?.besitzer != null)
            rahmenFarbe
        else if (strich != null)
            strichOhneBesitzerFarbe
        else
            rahmenFarbe
    }

    fun aktualisiereAnzeige() {
        postInvalidate() // View zwingen, neu zu zeichnen
    }

    companion object {
        const val MULTI_0_25 = 0.25f
        const val MULTI_0_75 = 0.75f
        const val MULTI_1_25 = 1.25f
        const val INITIAL_KAESTCHEN_SEITENLAENGE_PIXEL = 50f
        const val PADDING_PX = 10f
        const val SYMBOL_PADDING_PX = 12
        const val ECKPUNKT_RADIUS_PX = 4f
        const val STRICH_DICKE_PX = 8f
    }
}
