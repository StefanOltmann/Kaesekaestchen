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
import java.util.Collections;
import java.util.List;

/**
 * Der SpielerManager bestimmt, welcher Spieler am Zug ist und wählt den
 * nächsten Spieler aus. Er kennt alle Mitspieler.
 * 
 * @author Stefan Oltmann
 */
public class SpielerManager {

    /** Liste aller Spieler. */
    private List<Spieler> spielerListe = new ArrayList<Spieler>();

    /** Der Spieler, der gerade am Zug ist. */
    private Spieler       aktuellerSpieler;

    public SpielerManager() {
    }

    public void addSpieler(Spieler spieler) {
        spielerListe.add(spieler);
    }

    public List<Spieler> getSpieler() {
        return Collections.unmodifiableList(spielerListe);
    }

    public Spieler getAktuellerSpieler() {
        if (aktuellerSpieler == null)
            throw new RuntimeException("Vor Abfrage des Spielers muss 'neuerZug' mindestens einmal aufgerufen worden sein!");
        return aktuellerSpieler;
    }

    public void naechstenSpielerAuswaehlen() {

        int indexAktSpieler = spielerListe.indexOf(aktuellerSpieler);

        int indexNaechsterSpieler = indexAktSpieler + 1;
        if (indexNaechsterSpieler > spielerListe.size() - 1)
            indexNaechsterSpieler = 0;

        aktuellerSpieler = spielerListe.get(indexNaechsterSpieler);
    }

}
