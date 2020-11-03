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
package de.stefan_oltmann.kaesekaestchen.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/** Der Splashscreen ist eine eigene Activity. */
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        /*
         * Wenn diese Methode aufgerufen wird hat die App ihren Kaltstart
         * abgeschlossen und wir leiten weiter zur Haupt-Activity. Wir
         * wollen hier einen Splashscreen, der nur solange angezeigt wird,
         * wie die App tatsächlich zum Starten benötigt.
         */

        super.onCreate(savedInstanceState)

        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)

        startActivity(intent)

        finish()
    }
}