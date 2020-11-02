package de.stefan_oltmann.kaesekaestchen.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import de.stefan_oltmann.kaesekaestchen.controller.GameLoop
import de.stefan_oltmann.kaesekaestchen.controller.GameLoopCallback
import de.stefan_oltmann.kaesekaestchen.controller.SpielerManager
import de.stefan_oltmann.kaesekaestchen.databinding.FragmentSpielBinding
import de.stefan_oltmann.kaesekaestchen.model.*
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class SpielFragment : Fragment(), GameLoopCallback {

    companion object {

        private const val AUSGEWAEHLT_ALPHA = 1.0f
        private const val AUSGEGRAUT_ALPHA = 0.1f
    }

    private val args: SpielFragmentArgs by navArgs()

    private val handler = Handler(Looper.myLooper()!!)

    private lateinit var binding: FragmentSpielBinding

    private lateinit var gameLoop : GameLoop

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentSpielBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        gameLoop = GameLoop(this)

        binding.spielfeldView.init(gameLoop)

        val spielModus = SpielModus.valueOf(args.spielModus)
        val feldGroesse = FeldGroesse.valueOf(args.feldGroesse)

        gameLoop.start(spielModus, feldGroesse)

        return binding.root
    }

    override fun onStop() {
        gameLoop.stop()
        super.onStop()
    }

    override fun onSpielerIstAnDerReihe(spieler: Spieler) {

        /*
         * Der Spieler der gerade nicht dran ist bekommt
         * eine halb-transparente Anzeige.
         */
        handler.post {

            binding.spielKaeseImageView.alpha = if (spieler == Spieler.MAUS) AUSGEGRAUT_ALPHA else AUSGEWAEHLT_ALPHA
            binding.spielMausImageView.alpha = if (spieler == Spieler.KAESE) AUSGEGRAUT_ALPHA else AUSGEWAEHLT_ALPHA
        }
    }

    override fun onSpielBeendet(gewinner: Spieler, punktestandKaese: Int, punktestandMaus: Int) {

        val action = SpielFragmentDirections.actionNavSpielToGewonnenFragment(
            gewinnerSpieler = gewinner.toString(),
            punktestandKaese = punktestandKaese,
            punktestandMaus = punktestandMaus
        )

        NavHostFragment.findNavController(this@SpielFragment).navigate(action)
    }

    override fun aktualisiereSpielfeldViewAnzeige() {

        binding.spielfeldView.aktualisiereAnzeige()
    }
}