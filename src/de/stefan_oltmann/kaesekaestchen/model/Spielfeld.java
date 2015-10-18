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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Das Spielfeld.
 * 
 * @author Stefan Oltmann
 */
public class Spielfeld {

    private int             breiteInKaestchen;
    private int             hoeheInKaestchen;

    private Kaestchen[][]   kaestchenArray;

    /**
     * Aus Performance-Gründen wird eine zweite Liste geführt, damit die
     * 'kaestchenListe' nicht so häufig durch-iteriert werden muss. Dies führt
     * bei großen Feldern zu schlechter Performance.
     */
    private List<Kaestchen> offeneKaestchen     = new ArrayList<Kaestchen>();

    private Set<Strich>     stricheOhneBesitzer = new HashSet<Strich>();

    /**
     * Der Konstuktor ist private, da zum Erstellen von Spielfeldern die Factory
     * Method generieren() verwendet werden soll.
     */
    private Spielfeld(int breiteInKaestchen, int hoeheInKaestchen) {
        this.breiteInKaestchen = breiteInKaestchen;
        this.hoeheInKaestchen = hoeheInKaestchen;

        this.kaestchenArray = new Kaestchen[breiteInKaestchen][hoeheInKaestchen];
    }

    public List<Kaestchen> getKaestchenListe() {

        List<Kaestchen> liste = new ArrayList<Kaestchen>();

        for (int rasterX = 0; rasterX < breiteInKaestchen; rasterX++) {
            for (int rasterY = 0; rasterY < hoeheInKaestchen; rasterY++) {
                liste.add(kaestchenArray[rasterX][rasterY]);
            }
        }

        return Collections.unmodifiableList(liste);
    }

    public List<Kaestchen> getOffeneKaestchenListe() {
        return Collections.unmodifiableList(offeneKaestchen);
    }

    public Set<Strich> getStricheOhneBesitzer() {
        return Collections.unmodifiableSet(stricheOhneBesitzer);
    }

    private void addKaestchen(Kaestchen kaestchen) {
        kaestchenArray[kaestchen.getRasterX()][kaestchen.getRasterY()] = kaestchen;
        offeneKaestchen.add(kaestchen);
    }

    private void addStrich(Strich strich) {
        stricheOhneBesitzer.add(strich);
    }

    public Kaestchen getKaestchen(int rasterX, int rasterY) {

        if (rasterX >= breiteInKaestchen || rasterY >= hoeheInKaestchen)
            return null;

        return kaestchenArray[rasterX][rasterY];
    }

    public int getBreiteInKaestchen() {
        return breiteInKaestchen;
    }

    public int getHoeheInKaestchen() {
        return hoeheInKaestchen;
    }

    /**
     * Schließt alle Kästchen, die geschlossen werden können.
     * 
     * @param zuzuweisenderBesitzer
     *            Der zuzuweisende Besitzer dieser Kästchen
     * @return Konnte ein Kästchen geschlossen werden? (Wichtig für Spielablauf)
     */
    private boolean schliesseAlleMoeglichenKaestchen(Spieler zuzuweisenderBesitzer) {

        boolean kaestchenKonnteGeschlossenWerden = false;

        Iterator<Kaestchen> offeneKaestchenIt = offeneKaestchen.iterator();

        while (offeneKaestchenIt.hasNext()) {

            Kaestchen kaestchen = offeneKaestchenIt.next();

            if (kaestchen.isAlleStricheHabenBesitzer() && kaestchen.getBesitzer() == null) {
                kaestchen.setBesitzer(zuzuweisenderBesitzer);
                offeneKaestchenIt.remove();
                kaestchenKonnteGeschlossenWerden = true;
            }
        }

        return kaestchenKonnteGeschlossenWerden;
    }

    public boolean isAlleKaestchenHabenBesitzer() {
        return offeneKaestchen.isEmpty();
    }

    public boolean waehleStrich(Strich strich, Spieler spieler) {
        strich.setBesitzer(spieler);
        stricheOhneBesitzer.remove(strich);
        return schliesseAlleMoeglichenKaestchen(spieler);
    }

    /**
     * Factory Method zur Erzeugung eines Spielfeldes
     */
    public static Spielfeld generieren(int anzahlH, int anzahlV) {

        Spielfeld spielfeld = new Spielfeld(anzahlH, anzahlV);

        for (int rasterX = 0; rasterX < anzahlH; rasterX++) {
            for (int rasterY = 0; rasterY < anzahlV; rasterY++) {

                spielfeld.addKaestchen(new Kaestchen(rasterX, rasterY));
            }
        }

        for (int rasterX = 0; rasterX < anzahlH; rasterX++) {
            for (int rasterY = 0; rasterY < anzahlV; rasterY++) {

                Kaestchen kaestchen = spielfeld.getKaestchen(rasterX, rasterY);

                Kaestchen kaestchenUnten = null;
                Kaestchen kaestchenRechts = null;

                if (rasterY < anzahlV - 1)
                    kaestchenUnten = spielfeld.getKaestchen(rasterX, rasterY + 1);

                if (rasterX < anzahlH - 1)
                    kaestchenRechts = spielfeld.getKaestchen(rasterX + 1, rasterY);

                Strich strichUnten = new Strich(kaestchen, kaestchenUnten, null, null);
                Strich strichRechts = new Strich(null, null, kaestchen, kaestchenRechts);

                if (kaestchenRechts != null) {
                    kaestchen.setStrichRechts(strichRechts);
                    kaestchenRechts.setStrichLinks(strichRechts);
                    spielfeld.addStrich(strichRechts);
                }

                if (kaestchenUnten != null) {
                    kaestchen.setStrichUnten(strichUnten);
                    kaestchenUnten.setStrichOben(strichUnten);
                    spielfeld.addStrich(strichUnten);
                }
            }
        }

        return spielfeld;
    }

}
