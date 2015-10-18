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
package de.stefan_oltmann.kaesekaestchen.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import de.stefan_oltmann.kaesekaestchen.SpielfeldView;

/**
 * Ein Kästchen auf dem Spielfeld.
 * 
 * @author Stefan Oltmann
 */
public class Kaestchen {

    /*
     * Position des Kästchen im Raster. Dieses wird hierüber identifiziert,
     * damit ist dies auch die ID.
     */
    private int     rasterX;
    private int     rasterY;

    /**
     * Konnte ein Spieler ein Kästchen schließen, wird er der Besitzer des
     * Kästchens. Dies zählt am Ende des Spiels als 1 Siegpunkt.
     */
    private Spieler besitzer;

    /* Striche des Kästchens */
    private Strich  strichOben;
    private Strich  strichUnten;
    private Strich  strichLinks;
    private Strich  strichRechts;

    private Paint   rahmenPaint = new Paint();

    /**
     * Konstruktor zum Erstellen des Kästchen. Es muss die Position/ID des
     * Kästchen angegeben werden.
     */
    public Kaestchen(int rasterX, int rasterY) {
        this.rasterX = rasterX;
        this.rasterY = rasterY;

        rahmenPaint.setStyle(Paint.Style.STROKE);
        rahmenPaint.setStrokeWidth(5);
    }

    public int getRasterX() {
        return rasterX;
    }

    public int getRasterY() {
        return rasterY;
    }

    public int getPixelX() {
        return rasterX * SpielfeldView.KAESTCHEN_SEITENLAENGE + SpielfeldView.PADDING;
    }

    public int getPixelY() {
        return rasterY * SpielfeldView.KAESTCHEN_SEITENLAENGE + SpielfeldView.PADDING;
    }

    public Spieler getBesitzer() {
        return besitzer;
    }

    public void setBesitzer(Spieler besitzer) {
        this.besitzer = besitzer;
    }

    public Strich getStrichOben() {
        return strichOben;
    }

    public void setStrichOben(Strich strichOben) {
        this.strichOben = strichOben;
    }

    public Strich getStrichUnten() {
        return strichUnten;
    }

    public void setStrichUnten(Strich strichUnten) {
        this.strichUnten = strichUnten;
    }

    public Strich getStrichLinks() {
        return strichLinks;
    }

    public void setStrichLinks(Strich strichLinks) {
        this.strichLinks = strichLinks;
    }

    public Strich getStrichRechts() {
        return strichRechts;
    }

    public void setStrichRechts(Strich strichRechts) {
        this.strichRechts = strichRechts;
    }

    public List<Strich> getStriche() {

        List<Strich> striche = new ArrayList<Strich>();
        if (strichOben != null)
            striche.add(strichOben);
        if (strichUnten != null)
            striche.add(strichUnten);
        if (strichLinks != null)
            striche.add(strichLinks);
        if (strichRechts != null)
            striche.add(strichRechts);
        return striche;
    }

    public List<Strich> getStricheOhneBesitzer() {

        List<Strich> striche = new ArrayList<Strich>();
        if (strichOben != null && strichOben.getBesitzer() == null)
            striche.add(strichOben);
        if (strichUnten != null && strichUnten.getBesitzer() == null)
            striche.add(strichUnten);
        if (strichLinks != null && strichLinks.getBesitzer() == null)
            striche.add(strichLinks);
        if (strichRechts != null && strichRechts.getBesitzer() == null)
            striche.add(strichRechts);
        return striche;
    }

    public boolean isAlleStricheHabenBesitzer() {
        return getStricheOhneBesitzer().size() == 0;
    }

    public Rect getRectStrichOben() {

        if (strichOben == null)
            return null;

        return new Rect(getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4, getPixelY() - SpielfeldView.KAESTCHEN_SEITENLAENGE / 4, getPixelX() + (int) (SpielfeldView.KAESTCHEN_SEITENLAENGE * 0.75), getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4);
    }

    public Rect getRectStrichUnten() {

        if (strichUnten == null)
            return null;

        return new Rect(getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4, getPixelY() + (int) (SpielfeldView.KAESTCHEN_SEITENLAENGE * 0.75), getPixelX() + (int) (SpielfeldView.KAESTCHEN_SEITENLAENGE * 0.75), getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4);
    }

    public Rect getRectStrichLinks() {

        if (strichLinks == null)
            return null;

        return new Rect(getPixelX() - SpielfeldView.KAESTCHEN_SEITENLAENGE / 4, getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4, getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4, getPixelY() + (int) (SpielfeldView.KAESTCHEN_SEITENLAENGE * 0.75));
    }

    public Rect getRectStrichRechts() {

        if (strichRechts == null)
            return null;

        return new Rect(getPixelX() + (int) (SpielfeldView.KAESTCHEN_SEITENLAENGE * 0.75), getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4, getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE + SpielfeldView.KAESTCHEN_SEITENLAENGE / 4, getPixelY() + (int) (SpielfeldView.KAESTCHEN_SEITENLAENGE * 0.75));
    }

    /**
     * Diese Methode bestimmt, auf welchen Strich des Kästchen gedrückt wurde.
     */
    public Strich ermittleStrich(int pixelX, int pixelY) {

        if (getRectStrichOben() != null && getRectStrichOben().contains(pixelX, pixelY))
            return strichOben;

        if (getRectStrichUnten() != null && getRectStrichUnten().contains(pixelX, pixelY))
            return strichUnten;

        if (getRectStrichLinks() != null && getRectStrichLinks().contains(pixelX, pixelY))
            return strichLinks;

        if (getRectStrichRechts() != null && getRectStrichRechts().contains(pixelX, pixelY))
            return strichRechts;

        return null;
    }

    public void onDraw(Canvas canvas) {

        if (besitzer != null) {

            Paint fuellungPaint = new Paint();
            fuellungPaint.setColor(besitzer.getFarbe());

            Rect destRect = new Rect(getPixelX(), getPixelY(), getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE, getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE);
            canvas.drawBitmap(besitzer.getSymbol(), null, destRect, rahmenPaint);
        }

        if (strichOben == null) {
            rahmenPaint.setColor(Color.BLACK);
            canvas.drawLine(getPixelX(), getPixelY(), getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE, getPixelY(), rahmenPaint);
        }

        if (strichUnten != null && strichUnten.getBesitzer() != null)
            rahmenPaint.setColor(strichUnten.getBesitzer().getFarbe());
        else if (strichUnten != null)
            rahmenPaint.setColor(Color.LTGRAY);
        else
            rahmenPaint.setColor(Color.BLACK);

        canvas.drawLine(getPixelX(), getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE, getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE, getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE, rahmenPaint);

        if (strichLinks == null) {
            rahmenPaint.setColor(Color.BLACK);
            canvas.drawLine(getPixelX(), getPixelY(), getPixelX(), getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE, rahmenPaint);
        }

        if (strichRechts != null && strichRechts.getBesitzer() != null)
            rahmenPaint.setColor(strichRechts.getBesitzer().getFarbe());
        else if (strichRechts != null)
            rahmenPaint.setColor(Color.LTGRAY);
        else
            rahmenPaint.setColor(Color.BLACK);

        canvas.drawLine(getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE, getPixelY(), getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE, getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE, rahmenPaint);

        /* Eckpunkte zeichnen */
        rahmenPaint.setColor(Color.BLACK);
        canvas.drawRect(getPixelX() - 1, getPixelY() - 1, getPixelX() + 1, getPixelY() + 1, rahmenPaint);
        canvas.drawRect(getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE - 1, getPixelY() - 1, getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE + 1, getPixelY() + 1, rahmenPaint);
        canvas.drawRect(getPixelX() - 1, getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE - 1, getPixelX() + 1, getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE + 1, rahmenPaint);
        canvas.drawRect(getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE - 1, getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE - 1, getPixelX() + SpielfeldView.KAESTCHEN_SEITENLAENGE + 1, getPixelY() + SpielfeldView.KAESTCHEN_SEITENLAENGE + 1, rahmenPaint);
    }

    @Override
    public String toString() {
        return "Kaestchen [rasterX=" + rasterX + ", rasterY=" + rasterY + ", besitzer=" + besitzer + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + rasterX;
        result = prime * result + rasterY;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Kaestchen other = (Kaestchen) obj;
        if (rasterX != other.rasterX)
            return false;
        if (rasterY != other.rasterY)
            return false;
        return true;
    }

}
