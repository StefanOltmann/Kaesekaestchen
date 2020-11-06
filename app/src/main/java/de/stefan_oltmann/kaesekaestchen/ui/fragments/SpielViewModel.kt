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
import de.stefan_oltmann.kaesekaestchen.controller.SpielLogik
import de.stefan_oltmann.kaesekaestchen.model.Spieler
import de.stefan_oltmann.kaesekaestchen.model.Spielfeld

class SpielViewModel : ViewModel() {

    companion object {

        private const val AUSGEWAEHLT_ALPHA = 1.0f
        private const val AUSGEGRAUT_ALPHA = 0.1f
    }

    val spielfeld = MutableLiveData<Spielfeld>()
    val spielLogik = MutableLiveData<SpielLogik>()

    val aktuellerSpieler = MutableLiveData<Spieler>()

    val spielKaeseImageViewAlpha = Transformations.map(aktuellerSpieler) {
        if (it == Spieler.KAESE) AUSGEGRAUT_ALPHA else AUSGEWAEHLT_ALPHA
    }

    val spielMausImageViewAlpha = Transformations.map(aktuellerSpieler) {
        if (it == Spieler.MAUS) AUSGEGRAUT_ALPHA else AUSGEWAEHLT_ALPHA
    }

    /**
     * Wenn das ViewModel vernichtet wird müssen wir die
     * Logik darüber informieren, damit der Courotines Job
     * sauber entfernt werden kann.
     */
    override fun onCleared() {
        spielLogik.value?.onCleared()
    }
}