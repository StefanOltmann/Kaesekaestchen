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

import android.graphics.Bitmap;

/**
 * Diese Klasse repräsentiert einen Spieler. Dieser hat einen Namen, eine Farbe
 * und Symbol. Er kann entweder menschlich sein oder die KI.
 * 
 * @author Stefan Oltmann
 */
public class Spieler {

    private String     name;
    private Bitmap     symbol;
    private int        farbe;
    private SpielerTyp spielerTyp;

    public Spieler(String name, Bitmap symbol, int farbe, SpielerTyp spielerTyp) {
        this.name = name;
        this.symbol = symbol;
        this.farbe = farbe;
        this.spielerTyp = spielerTyp;
    }

    public String getName() {
        return name;
    }

    public Bitmap getSymbol() {
        return symbol;
    }

    public int getFarbe() {
        return farbe;
    }

    public SpielerTyp getSpielerTyp() {
        return spielerTyp;
    }

    public boolean isComputerGegner() {
        return spielerTyp.isComputerGegner();
    }

    @Override
    public String toString() {
        return "Spieler [name=" + name + ", farbe=" + farbe + "]";
    }

}
