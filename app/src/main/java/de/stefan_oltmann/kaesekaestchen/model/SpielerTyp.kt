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

/**
 * Spieler können entweder Menschen sein oder eine von 3 KI-Stärken.
 *
 * @author Stefan Oltmann
 */
enum class SpielerTyp {

    MENSCH,
    COMPUTER_EINFACH,
    COMPUTER_MITTEL,
    COMPUTER_SCHWER;

    val isComputerGegner: Boolean
        get() = this == COMPUTER_EINFACH || this == COMPUTER_MITTEL || this == COMPUTER_SCHWER

    companion object {

        /**
         * Wenn im Spinner ein Typ ausgewählt wurde, können wir nur die
         * String-Repräsentation davon abfragen. Der Spinner nimmt leider keine
         * Enums entgegen. Deshalb müssen wir den richtigen Typ aus dem String
         * parsen.
         */
        @JvmStatic
        fun parseStringToSpielerTyp(string: String): SpielerTyp {

            if (string == "Mensch" || string == "Human")
                return MENSCH

            if (string == "KI Leicht" || string == "KI Easy")
                return COMPUTER_EINFACH

            if (string == "KI Mittel" || string == "KI Medium")
                return COMPUTER_MITTEL

            if (string == "KI Schwer" || string == "KI Hard")
                return COMPUTER_SCHWER

            throw IllegalArgumentException("Unbekannter SpielerTyp: $string")
        }
    }
}