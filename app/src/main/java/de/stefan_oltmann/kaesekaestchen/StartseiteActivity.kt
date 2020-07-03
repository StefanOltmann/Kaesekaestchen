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
package de.stefan_oltmann.kaesekaestchen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Spinner
import de.stefan_oltmann.kaesekaestchen.model.SpielerTyp
import de.stefan_oltmann.kaesekaestchen.model.SpielerTyp.Companion.parseStringToSpielerTyp

/**
 * Diese Activity wird bei Starten der App angezeigt. Hier wird ausgewählt, wer
 * die beiden Mitspieler sind.
 *
 * @author Stefan Oltmann
 */
class StartseiteActivity : Activity(), View.OnClickListener {

    companion object {
        /**
         * Wenn eine App Einstellungen speichert, dann müssen die in einer
         * "shared preferences"-Map unter einem bestimmten Schlüssel abgelegt
         * werden. In diesem Fall speichern wir die Spiel-Einstellungen.
         */
        const val GAME_SETTINGS_KEY = "game_settings"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.startseite)

        val spielenButton = findViewById<View>(R.id.spielen) as Button

        spielenButton.setOnClickListener(this)

        /* Aus gespeicherten Einstellungen die View wieder aufbauen. */
        val settings = getSharedPreferences(GAME_SETTINGS_KEY, Context.MODE_PRIVATE)
        (findViewById<View>(R.id.spieler_typ_1_spinner) as Spinner).setSelection(settings.getInt("spielerTyp1", 0))
        (findViewById<View>(R.id.spieler_typ_2_spinner) as Spinner).setSelection(settings.getInt("spielerTyp2", 2))
        (findViewById<View>(R.id.feld_groesse_x) as Spinner).setSelection(settings.getInt("feldGroesseX", 3))
        (findViewById<View>(R.id.feld_groesse_y) as Spinner).setSelection(settings.getInt("feldGroesseY", 3))
    }

    /**
     * Diese Methode muss überschrieben werden, wenn ein Menü angezeigt werden
     * soll. Die App benutzt dieses um ein Beenden-Menü anzubieten.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.hauptmenue, menu)

        return true
    }

    /**
     * Wurde in der Menüleiste eine Option gewählt, wird diese Methode
     * aufgerufen.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.app_beenden)
            finish()

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {

        val spielerTyp1 =
            parseStringToSpielerTyp((findViewById<View>(R.id.spieler_typ_1_spinner) as Spinner).selectedItem as String)

        val spielerTyp2 =
            parseStringToSpielerTyp((findViewById<View>(R.id.spieler_typ_2_spinner) as Spinner).selectedItem as String)

        val feldGroesseX: Int =
            ((findViewById<View>(R.id.feld_groesse_x) as Spinner).selectedItem as String).toInt()

        val feldGroesseY: Int =
            ((findViewById<View>(R.id.feld_groesse_y) as Spinner).selectedItem as String).toInt()

        /*
         * Werte in Settings speichern
         */

        val settings = getSharedPreferences(GAME_SETTINGS_KEY, Context.MODE_PRIVATE)

        val editor = settings.edit()

        editor.putInt(
            "spielerTyp1",
            (findViewById<View>(R.id.spieler_typ_1_spinner) as Spinner).selectedItemPosition
        )

        editor.putInt(
            "spielerTyp2",
            (findViewById<View>(R.id.spieler_typ_2_spinner) as Spinner).selectedItemPosition
        )

        editor.putInt(
            "feldGroesseX",
            (findViewById<View>(R.id.feld_groesse_x) as Spinner).selectedItemPosition
        )

        editor.putInt(
            "feldGroesseY",
            (findViewById<View>(R.id.feld_groesse_y) as Spinner).selectedItemPosition
        )

        editor.apply()

        /*
         * Intent bauen und absetzen
         */

        buildIntentAndStartActivity(spielerTyp1, spielerTyp2, feldGroesseX, feldGroesseY)
    }

    private fun buildIntentAndStartActivity(
        spielerTyp1: SpielerTyp, spielerTyp2: SpielerTyp,
        feldGroesseX: Int, feldGroesseY: Int) {

        val intent = Intent(this, SpielActivity::class.java)

        intent.putExtra("spielerTyp1", spielerTyp1)
        intent.putExtra("spielerTyp2", spielerTyp2)
        intent.putExtra("feldGroesseX", feldGroesseX)
        intent.putExtra("feldGroesseY", feldGroesseY)

        startActivity(intent)
    }
}