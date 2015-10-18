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
 * Das Spielprinzip besteht daraus, Striche zu setzen um Kästchen zu schließen.
 * Diese Klasse repräsentiert einen solchen Strich.
 * 
 * @author Stefan Oltmann
 */
public class Strich {

    /* ID des Striches */
    private Kaestchen       kaestchenOben;
    private Kaestchen       kaestchenUnten;
    private Kaestchen       kaestchenLinks;
    private Kaestchen       kaestchenRechts;

    /* Auflistung zum Durch-iterieren */
    private List<Kaestchen> kaestchenListe = new ArrayList<Kaestchen>();

    private Spieler         besitzer;

    public Strich(Kaestchen kaestchenOben, Kaestchen kaestchenUnten,
            Kaestchen kaestchenLinks, Kaestchen kaestchenRechts) {

        this.kaestchenOben = kaestchenOben;
        this.kaestchenUnten = kaestchenUnten;
        this.kaestchenLinks = kaestchenLinks;
        this.kaestchenRechts = kaestchenRechts;

        if (kaestchenOben != null)
            kaestchenListe.add(kaestchenOben);

        if (kaestchenUnten != null)
            kaestchenListe.add(kaestchenUnten);

        if (kaestchenLinks != null)
            kaestchenListe.add(kaestchenLinks);

        if (kaestchenRechts != null)
            kaestchenListe.add(kaestchenRechts);
    }

    public Kaestchen getKaestchenOben() {
        return kaestchenOben;
    }

    public Kaestchen getKaestchenUnten() {
        return kaestchenUnten;
    }

    public Kaestchen getKaestchenLinks() {
        return kaestchenLinks;
    }

    public Kaestchen getKaestchenRechts() {
        return kaestchenRechts;
    }

    public List<Kaestchen> getKaestchenListe() {
        return Collections.unmodifiableList(kaestchenListe);
    }

    /**
     * Wenn eines der Kästchen um diesen Strich nur noch zwei Besitzer hat, dann
     * h#tte es nach dem Setzen dieses Striches nur noch einen... Damit würde
     * man dem Gegner ein Kästchen schenken.
     */
    public boolean isKoennteUmliegendendesKaestchenSchliessen() {

        for (Kaestchen kaestchen : kaestchenListe)
            if (kaestchen.getStricheOhneBesitzer().size() <= 2)
                return true;

        return false;
    }

    public Spieler getBesitzer() {
        return besitzer;
    }

    public void setBesitzer(Spieler besitzer) {
        this.besitzer = besitzer;
    }

    @Override
    public String toString() {
        return "Strich [kaestchenOben=" + kaestchenOben + ", kaestchenUnten="
                + kaestchenUnten + ", kaestchenLinks=" + kaestchenLinks
                + ", kaestchenRechts=" + kaestchenRechts + ", besitzer="
                + besitzer + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((kaestchenLinks == null) ? 0 : kaestchenLinks.hashCode());
        result = prime * result + ((kaestchenOben == null) ? 0 : kaestchenOben.hashCode());
        result = prime * result + ((kaestchenRechts == null) ? 0 : kaestchenRechts.hashCode());
        result = prime * result + ((kaestchenUnten == null) ? 0 : kaestchenUnten.hashCode());
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
        Strich other = (Strich) obj;
        if (kaestchenLinks == null) {
            if (other.kaestchenLinks != null)
                return false;
        } else if (!kaestchenLinks.equals(other.kaestchenLinks))
            return false;
        if (kaestchenOben == null) {
            if (other.kaestchenOben != null)
                return false;
        } else if (!kaestchenOben.equals(other.kaestchenOben))
            return false;
        if (kaestchenRechts == null) {
            if (other.kaestchenRechts != null)
                return false;
        } else if (!kaestchenRechts.equals(other.kaestchenRechts))
            return false;
        if (kaestchenUnten == null) {
            if (other.kaestchenUnten != null)
                return false;
        } else if (!kaestchenUnten.equals(other.kaestchenUnten))
            return false;
        return true;
    }

}
