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

    /**
     * Wenn eine App Einstellungen speichert, dann müssen die in einer
     * "shared preferences"-Map unter einem bestimmten Schlüssel abgelegt
     * werden. In diesem Fall speichern wir die Spiel-Einstellungen.
     */
    private val gameSettings
        get() = getSharedPreferences("game_settings", Context.MODE_PRIVATE)

    private val spielerTyp1Spinner
        get() = findViewById<View>(R.id.spieler_typ_1_spinner) as Spinner

    private val spielerTyp2Spinner
        get() = findViewById<View>(R.id.spieler_typ_2_spinner) as Spinner

    private val feldGroesseXSpinner
        get() = findViewById<View>(R.id.feld_groesse_x) as Spinner

    private val feldGroesseYSpinner
        get() = findViewById<View>(R.id.feld_groesse_y) as Spinner

    private val spielenButton
        get() = findViewById<View>(R.id.spielen) as Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.startseite)

        spielenButton.setOnClickListener(this)

        restoreGameSettings()
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

        saveGameSettings()

        val spielerTyp1 = parseStringToSpielerTyp(spielerTyp1Spinner.selectedItem as String)
        val spielerTyp2 = parseStringToSpielerTyp(spielerTyp2Spinner.selectedItem as String)
        val feldGroesseX = (feldGroesseXSpinner.selectedItem as String).toInt()
        val feldGroesseY = (feldGroesseYSpinner.selectedItem as String).toInt()

        startSpielActivity(spielerTyp1, spielerTyp2, feldGroesseX, feldGroesseY)
    }

    /*
     * Baut einen {@link Intent} zusammen auf Basis der ausgewählten Daten
     * und startet die {@link SpielActivity} damit.
     */
    private fun startSpielActivity(
        spielerTyp1: SpielerTyp,
        spielerTyp2: SpielerTyp,
        feldGroesseX: Int,
        feldGroesseY: Int) {

        val intent = Intent(this, SpielActivity::class.java)

        intent.putExtra("spielerTyp1", spielerTyp1)
        intent.putExtra("spielerTyp2", spielerTyp2)
        intent.putExtra("feldGroesseX", feldGroesseX)
        intent.putExtra("feldGroesseY", feldGroesseY)

        startActivity(intent)
    }

    /*
     * Setzt der View die gespeicherten Game-Settings
     */
    private fun restoreGameSettings() {

        spielerTyp1Spinner.setSelection(gameSettings.getInt("spielerTyp1", 0))
        spielerTyp2Spinner.setSelection(gameSettings.getInt("spielerTyp2", 2))
        feldGroesseXSpinner.setSelection(gameSettings.getInt("feldGroesseX", 3))
        feldGroesseYSpinner.setSelection(gameSettings.getInt("feldGroesseY", 3))
    }

    /*
     * Speichert die aktuellen Einstellugen in der View in die Game-Settings
     */
    private fun saveGameSettings() {

        val gameSettingsEditor = gameSettings.edit()

        gameSettingsEditor.putInt("spielerTyp1", spielerTyp1Spinner.selectedItemPosition)
        gameSettingsEditor.putInt("spielerTyp2", spielerTyp2Spinner.selectedItemPosition)
        gameSettingsEditor.putInt("feldGroesseX", feldGroesseXSpinner.selectedItemPosition)
        gameSettingsEditor.putInt("feldGroesseY", feldGroesseYSpinner.selectedItemPosition)

        gameSettingsEditor.apply()
    }
}