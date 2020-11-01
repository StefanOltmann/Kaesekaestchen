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
package de.stefan_oltmann.kaesekaestchen.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import de.stefan_oltmann.kaesekaestchen.R
import de.stefan_oltmann.kaesekaestchen.model.SpielerTyp

/**
 * Diese Activity wird bei Starten der App angezeigt. Hier wird ausgewählt, wer
 * die beiden Mitspieler sind.
 *
 * @author Stefan Oltmann
 */
class MainActivity : AppCompatActivity() {

    /**
     * Wenn eine App Einstellungen speichert, dann müssen die in einer
     * "shared preferences"-Map unter einem bestimmten Schlüssel abgelegt
     * werden. In diesem Fall speichern wir die Spiel-Einstellungen.
     */
    private val gameSettings
        get() = getSharedPreferences("game_settings", Context.MODE_PRIVATE)

    private val spieler1KiSwitch
        get() = findViewById<Switch>(R.id.spieler_1_ki_switch)

    private val spieler2KiSwitch
        get() = findViewById<Switch>(R.id.spieler_2_ki_switch)

    private val feldGroesseXSpinner
        get() = findViewById<Spinner>(R.id.feld_groesse_x)

    private val feldGroesseYSpinner
        get() = findViewById<Spinner>(R.id.feld_groesse_y)

    private val spielenButton
        get() = findViewById<Button>(R.id.spielen)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_main)

        spielenButton.setOnClickListener { onSpielenClick() }

        spieler1KiSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.text = getText(
                if (isChecked) R.string.spieler_typ_computer else R.string.spieler_typ_mensch)
        }

        spieler2KiSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.text = getText(
                if (isChecked) R.string.spieler_typ_computer else R.string.spieler_typ_mensch)
        }

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

    fun onSpielenClick() {

        saveGameSettings()

        val spielerTyp1 = if (spieler1KiSwitch.isChecked) SpielerTyp.COMPUTER else SpielerTyp.MENSCH
        val spielerTyp2 = if (spieler2KiSwitch.isChecked) SpielerTyp.COMPUTER else SpielerTyp.MENSCH

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

    override fun onPause() {
        super.onPause()

        saveGameSettings()
    }

    /*
     * Setzt der View die gespeicherten Game-Settings.
     */
    private fun restoreGameSettings() {

        /*
         * Da der View Eigenschaften gesetzt werden hier nochmal
         * sicherstellen, dass wir auf dem UI-Thread sind.
         */
        runOnUiThread {

            spieler1KiSwitch.isChecked = gameSettings.getBoolean("spieler_typ_1_ki", false)
            spieler2KiSwitch.isChecked = gameSettings.getBoolean("spieler_typ_2_ki", true)
            feldGroesseXSpinner.setSelection(gameSettings.getInt("feld_groesse_x", 6))
            feldGroesseYSpinner.setSelection(gameSettings.getInt("feld_groesse_y", 6))
        }
    }

    /*
     * Speichert die aktuellen Einstellugen in der View in die Game-Settings
     */
    private fun saveGameSettings() {

        val gameSettingsEditor = gameSettings.edit()

        gameSettingsEditor.putBoolean("spieler_typ_1_ki", spieler1KiSwitch.isChecked)
        gameSettingsEditor.putBoolean("spieler_typ_2_ki", spieler2KiSwitch.isChecked)
        gameSettingsEditor.putInt("feld_groesse_x", feldGroesseXSpinner.selectedItemPosition)
        gameSettingsEditor.putInt("feld_groesse_y", feldGroesseYSpinner.selectedItemPosition)

        gameSettingsEditor.apply()
    }
}