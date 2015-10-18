/*
 * Kaesekaestchen
 * A simple Dots'n'Boxes Game for Android
 *
 * Copyright (C) 2011 - 2012 Stefan Oltmann
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
package de.stefan_oltmann.kaesekaestchen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import de.stefan_oltmann.kaesekaestchen.model.Kaestchen;
import de.stefan_oltmann.kaesekaestchen.model.Spielfeld;
import de.stefan_oltmann.kaesekaestchen.model.Strich;

/**
 * Diese Klasse zeichnet das Spielfeld und nimmt Interaktionen des Benutzers
 * entgegen.
 * 
 * @author Stefan Oltmann
 */
public class SpielfeldView extends View implements OnTouchListener {

    public static int       KAESTCHEN_SEITENLAENGE = 50;
    public static int       PADDING                = 5;

    private Spielfeld       spielfeld;

    /**
     * Über die letzte Eingabe wird in Erfahrung gebracht, was der Nutzer
     * möchte. Der Abruf dieses Wertes ist sozusagen im Spiel-Ablauf blocking.
     */
    private volatile Strich letzteEingabe;

    public SpielfeldView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Spielfeld spielfeld) {
        this.spielfeld = spielfeld;
        setOnTouchListener(this);
    }

    public Strich getLetzteEingabe() {
        return letzteEingabe;
    }

    public void resetLetzteEingabe() {
        letzteEingabe = null;
    }

    /**
     * Wird die Bildschirmauflösung verändert oder initial bekanntgegen, wird
     * diese Methode aufgerufen. Wir benutzen das um zu ermitteln, wie groß ein
     * Kästchen in Abhängigkeit von der Auflösung des Displays sein muss.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (spielfeld == null)
            return;

        int maxBreite = (w - PADDING * 2) / spielfeld.getBreiteInKaestchen();
        int maxHoehe = (h - PADDING * 2) / spielfeld.getHoeheInKaestchen();
        KAESTCHEN_SEITENLAENGE = Math.min(maxBreite, maxHoehe);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(getResources().getColor(R.color.hintergrund_farbe));

        /*
         * Wurde das Spielfeld noch nicht initalisiert, dieses nicht zeichnen.
         * Ansonsten würde das zu einer NullPointer-Exception führen. Dies wird
         * auch benötigt, um korrekt im GUI-Editor dargestellt zu werden.
         */
        if (spielfeld == null) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), new Paint());
            return;
        }

        for (Kaestchen kaestchen : spielfeld.getKaestchenListe())
            kaestchen.onDraw(canvas);
    }

    public boolean onTouch(View view, MotionEvent event) {

        /*
         * Es gibt verschiedene MotionEvents, aber hier interessiert uns nur das
         * tatsächliche Drücken auf den Bildschirm.
         */
        if (event.getAction() != MotionEvent.ACTION_DOWN)
            return true;

        if (letzteEingabe != null)
            return true;

        int errechnetRasterX = (int) event.getX() / KAESTCHEN_SEITENLAENGE;
        int errechnetRasterY = (int) event.getY() / KAESTCHEN_SEITENLAENGE;

        Kaestchen kaestchen = spielfeld.getKaestchen(errechnetRasterX, errechnetRasterY);

        /*
         * Wenn sich an der berührten Position kein Kästchen befindet oder
         * dieses schon einen Besitzer hat, die Eingabe ignorieren.
         */
        if (kaestchen == null || kaestchen.getBesitzer() != null)
            return true;

        Strich strich = kaestchen.ermittleStrich((int) event.getX(), (int) event.getY());

        /*
         * Konnte kein Strich ermittelt werden, hat der Benutzer wahrscheinlich
         * die Mitte des Kästchens getroffen. Es ist jedenfalls nicht klar,
         * welchen Strich er gemeint hat. Deshalb wird die Eingabe abgebrochen.
         */
        if (strich == null)
            return true;

        /*
         * An dieser Stelle hat der Benutzer seine Eingabe erfolgreich getätigt.
         * Wir schreiben seine Eingabe in eine Zwischenvariable die zur
         * Kommunikation mit dem Gameloop-Thread verwendet wird und wecken
         * diesen via "notifyAll" wieder auf. Der Gameloop-Thread wurde zuvor
         * mit "wait()" und dieser Klasse als Semaphor "pausiert".
         */
        letzteEingabe = strich;

        synchronized (this) {
            this.notifyAll();
        }

        return true;
    }

    public void anzeigeAktualisieren() {
        postInvalidate(); // View zwingen, neu zu zeichnen
    }

}
