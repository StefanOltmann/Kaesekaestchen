package de.stefan_oltmann.kaesekaestchen.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
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

    /**
     * Wenn eine App Einstellungen speichert, dann müssen die in einer
     * "shared preferences"-Map unter einem bestimmten Schlüssel abgelegt
     * werden. In diesem Fall speichern wir die Spiel-Einstellungen.
     */
    private lateinit var gameSettings : SharedPreferences

    private lateinit var binding: FragmentStartBinding

    private var spielModus = SpielModus.EINZELSPIELER
    private var feldGroesse = FeldGroesse.KLEIN;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        gameSettings = requireContext().getSharedPreferences("game_settings", Context.MODE_PRIVATE)

        binding = FragmentStartBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        binding.einzelspielerImageButton.setOnClickListener {

            /* Den gewählen Modus merken. */
            spielModus = SpielModus.EINZELSPIELER

            /* Den anderen Modus-Button ausblenden. */
            binding.einzelspielerImageButton.alpha = AUSGEWAEHLT_ALPHA
            binding.mehrspielerImageButton.alpha = AUSGEGRAUT_ALPHA
        }

        binding.mehrspielerImageButton.setOnClickListener {

            /* Den gewählen Modus merken. */
            spielModus = SpielModus.MEHRSPIELER

            /* Den anderen Modus-Button ausblenden. */
            binding.einzelspielerImageButton.alpha = AUSGEGRAUT_ALPHA
            binding.mehrspielerImageButton.alpha = AUSGEWAEHLT_ALPHA
        }

        binding.feldGroesseSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                feldGroesse = FeldGroesse.values()[seekBar.progress]
            }
        })

        binding.spielenButton.setOnClickListener { onSpielenClick() }

        restoreGameSettings()

        return binding.root;
    }

    fun onSpielenClick() {

        saveGameSettings()

        val action =
            StartFragmentDirections.actionNavStartToNavSpiel(
                spielModus.toString(), feldGroesse.toString())

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

        gameSettings.getString(GAME_SETTINGS_KEY_SPIEL_MODUS, null)?.let {
            spielModus = SpielModus.valueOf(it)
        }

        gameSettings.getString(GAME_SETTINGS_KEY_FELD_GROESSE, null)?.let {
            feldGroesse = FeldGroesse.valueOf(it)
        }

        binding.einzelspielerImageButton.alpha =
            if (spielModus == SpielModus.EINZELSPIELER) AUSGEWAEHLT_ALPHA else AUSGEGRAUT_ALPHA

        binding.mehrspielerImageButton.alpha =
            if (spielModus == SpielModus.MEHRSPIELER) AUSGEWAEHLT_ALPHA else AUSGEGRAUT_ALPHA

        binding.feldGroesseSeekbar.progress = feldGroesse.ordinal
    }

    /*
     * Speichert die aktuellen Einstellugen in die Game-Settings
     */
    private fun saveGameSettings() {

        val gameSettingsEditor = gameSettings.edit()

        gameSettingsEditor.putString(GAME_SETTINGS_KEY_SPIEL_MODUS, spielModus.toString())
        gameSettingsEditor.putString(GAME_SETTINGS_KEY_FELD_GROESSE, feldGroesse.toString())

        gameSettingsEditor.apply()
    }
}