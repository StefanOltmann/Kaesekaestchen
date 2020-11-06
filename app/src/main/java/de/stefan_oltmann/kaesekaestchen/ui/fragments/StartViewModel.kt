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
package de.stefan_oltmann.kaesekaestchen.ui.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import de.stefan_oltmann.kaesekaestchen.model.SpielModus
import de.stefan_oltmann.kaesekaestchen.model.SpielfeldGroesse


class StartViewModel : ViewModel() {

    companion object {

        private const val AUSGEWAEHLT_ALPHA = 1.0f
        private const val AUSGEGRAUT_ALPHA = 0.1f
    }

    val spielModus = MutableLiveData(SpielModus.EINZELSPIELER)
    val feldGroesse = MutableLiveData(SpielfeldGroesse.KLEIN)

    fun setSpielModus(spielModus: SpielModus) {
        this.spielModus.value = spielModus
    }

    val einzelspielerImageButtonAlpha = Transformations.map(spielModus) {
        if (it == SpielModus.EINZELSPIELER) AUSGEWAEHLT_ALPHA else AUSGEGRAUT_ALPHA
    }

    val mehrspielerImageButtonAlpha = Transformations.map(spielModus) {
        if (it == SpielModus.MEHRSPIELER) AUSGEWAEHLT_ALPHA else AUSGEGRAUT_ALPHA
    }

    val feldGroesseSeekBarProgress = Transformations.map(feldGroesse) {
        feldGroesse.value!!.ordinal
    }
}