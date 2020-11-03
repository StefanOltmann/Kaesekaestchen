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

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import de.stefan_oltmann.kaesekaestchen.databinding.FragmentStartBinding
import de.stefan_oltmann.kaesekaestchen.model.FeldGroesse
import de.stefan_oltmann.kaesekaestchen.model.SpielModus

class StartFragment : Fragment() {

    companion object {

        private const val AUSGEWAEHLT_ALPHA = 1.0f
        private const val AUSGEGRAUT_ALPHA = 0.1f

        private const val GAME_SETTINGS_KEY_SPIEL_MODUS = "spiel_modus"
        private const val GAME_SETTINGS_KEY_FELD_GROESSE = "feld_groesse"
    }

    private val viewModel by viewModels<StartViewModel>()

    /**
     * Wenn eine App Einstellungen speichert, dann müssen die in einer
     * "shared preferences"-Map unter einem bestimmten Schlüssel abgelegt
     * werden. In diesem Fall speichern wir die Spiel-Einstellungen.
     */
    private lateinit var gameSettings : SharedPreferences

    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        gameSettings = requireContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)

        binding = FragmentStartBinding.inflate(inflater, container, false)

        /* Ein Binding sollte den LifeCycle immer kennen. */
        binding.lifecycleOwner = this

        binding.einzelspielerImageButton.setOnClickListener {

            /* Den gewählen Modus merken. */
            viewModel.spielModus.value = SpielModus.EINZELSPIELER

            setzeSpielModusButtonOptik()
        }

        binding.mehrspielerImageButton.setOnClickListener {

            /* Den gewählen Modus merken. */
            viewModel.spielModus.value = SpielModus.MEHRSPIELER

            setzeSpielModusButtonOptik()
        }

        binding.feldGroesseSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                /* Wird nicht benötigt, muss aber überschrieben werden. */
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                /* Wird nicht benötigt, muss aber überschrieben werden. */
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                viewModel.feldGroesse.value = FeldGroesse.values()[seekBar.progress]
            }
        })

        binding.spielenButton.setOnClickListener { onSpielenClick() }

        restoreGameSettings()

        return binding.root
    }

    /*
     * Setzt den Buttons zur Auswahl des Spiel-Modus die passende Optik
     */
    private fun setzeSpielModusButtonOptik() {

        binding.einzelspielerImageButton.alpha =
            if (viewModel.spielModus.value == SpielModus.EINZELSPIELER) AUSGEWAEHLT_ALPHA else AUSGEGRAUT_ALPHA

        binding.mehrspielerImageButton.alpha =
            if (viewModel.spielModus.value == SpielModus.MEHRSPIELER) AUSGEWAEHLT_ALPHA else AUSGEGRAUT_ALPHA
    }

    private fun onSpielenClick() {

        saveGameSettings()

        val action =
            StartFragmentDirections.actionNavStartToNavSpiel(
                viewModel.spielModus.value.toString(), viewModel.feldGroesse.value.toString())

        findNavController().navigate(action)
    }

    override fun onPause() {
        super.onPause()

        saveGameSettings()
    }

    /*
     * Setzt der View die gespeicherten Game-Settings.
     */
    private fun restoreGameSettings() {

        gameSettings.getString(GAME_SETTINGS_KEY_SPIEL_MODUS, null)?.let {
            viewModel.spielModus.value = SpielModus.valueOf(it)
        }

        gameSettings.getString(GAME_SETTINGS_KEY_FELD_GROESSE, null)?.let {
            viewModel.feldGroesse.value = FeldGroesse.valueOf(it)
        }

        setzeSpielModusButtonOptik()

        binding.feldGroesseSeekbar.progress = viewModel.feldGroesse.value!!.ordinal
    }

    /*
     * Speichert die aktuellen Einstellugen in die Game-Settings
     */
    private fun saveGameSettings() {

        val gameSettingsEditor = gameSettings.edit()

        gameSettingsEditor.putString(GAME_SETTINGS_KEY_SPIEL_MODUS, viewModel.spielModus.value.toString())
        gameSettingsEditor.putString(GAME_SETTINGS_KEY_FELD_GROESSE, viewModel.feldGroesse.value.toString())

        gameSettingsEditor.apply()
    }
}