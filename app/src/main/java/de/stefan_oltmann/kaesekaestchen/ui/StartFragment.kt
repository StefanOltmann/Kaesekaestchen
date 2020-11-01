package de.stefan_oltmann.kaesekaestchen.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import de.stefan_oltmann.kaesekaestchen.R
import de.stefan_oltmann.kaesekaestchen.databinding.FragmentSpielBinding
import de.stefan_oltmann.kaesekaestchen.databinding.FragmentStartBinding
import de.stefan_oltmann.kaesekaestchen.model.SpielerTyp

class StartFragment : Fragment() {

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

        binding.lifecycleOwner = this

        binding.spielenButton.setOnClickListener { onSpielenClick() }

        binding.spieler1KiSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.text = getText(
                if (isChecked) R.string.spieler_typ_computer else R.string.spieler_typ_mensch)
        }

        binding.spieler2KiSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            buttonView.text = getText(
                if (isChecked) R.string.spieler_typ_computer else R.string.spieler_typ_mensch)
        }

        restoreGameSettings()

        return binding.root;
    }

    fun onSpielenClick() {

        saveGameSettings()

        val spielerTyp1 = if (binding.spieler1KiSwitch.isChecked) SpielerTyp.COMPUTER else SpielerTyp.MENSCH
        val spielerTyp2 = if (binding.spieler2KiSwitch.isChecked) SpielerTyp.COMPUTER else SpielerTyp.MENSCH

        val feldGroesseX = (binding.feldGroesseXSpinner.selectedItem as String).toInt()
        val feldGroesseY = (binding.feldGroesseYSpinner.selectedItem as String).toInt()

        navigateToSpielFragment(spielerTyp1, spielerTyp2, feldGroesseX, feldGroesseY)
    }

    /*
     * Baut einen {@link Intent} zusammen auf Basis der ausgewählten Daten
     * und startet die {@link SpielActivity} damit.
     */
    private fun navigateToSpielFragment(
        spielerTyp1: SpielerTyp,
        spielerTyp2: SpielerTyp,
        feldGroesseX: Int,
        feldGroesseY: Int) {

        val action =
            StartFragmentDirections.actionNavStartToNavSpiel(
                spielerTyp1 == SpielerTyp.COMPUTER,
                spielerTyp2 == SpielerTyp.COMPUTER,
                feldGroesseX, feldGroesseY)

        NavHostFragment.findNavController(this).navigate(action)
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
        //runOnUiThread {

        binding.spieler1KiSwitch.isChecked = gameSettings.getBoolean("spieler_typ_1_ki", false)
        binding.spieler2KiSwitch.isChecked = gameSettings.getBoolean("spieler_typ_2_ki", true)
        binding.feldGroesseXSpinner.setSelection(gameSettings.getInt("feld_groesse_x", 6))
        binding.feldGroesseYSpinner.setSelection(gameSettings.getInt("feld_groesse_y", 6))
        //}
    }

    /*
     * Speichert die aktuellen Einstellugen in der View in die Game-Settings
     */
    private fun saveGameSettings() {

        val gameSettingsEditor = gameSettings.edit()

        gameSettingsEditor.putBoolean("spieler_typ_1_ki", binding.spieler1KiSwitch.isChecked)
        gameSettingsEditor.putBoolean("spieler_typ_2_ki", binding.spieler2KiSwitch.isChecked)
        gameSettingsEditor.putInt("feld_groesse_x", binding.feldGroesseXSpinner.selectedItemPosition)
        gameSettingsEditor.putInt("feld_groesse_y", binding.feldGroesseYSpinner.selectedItemPosition)

        gameSettingsEditor.apply()
    }
}