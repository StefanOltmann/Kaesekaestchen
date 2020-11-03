package de.stefan_oltmann.kaesekaestchen.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import de.stefan_oltmann.kaesekaestchen.controller.SpielLogik
import de.stefan_oltmann.kaesekaestchen.controller.SpielLogikCallback
import de.stefan_oltmann.kaesekaestchen.databinding.FragmentSpielBinding
import de.stefan_oltmann.kaesekaestchen.model.*

class SpielFragment : Fragment(), SpielLogikCallback {

    companion object {

        private const val AUSGEWAEHLT_ALPHA = 1.0f
        private const val AUSGEGRAUT_ALPHA = 0.1f
    }

    private val args: SpielFragmentArgs by navArgs()

    private val viewModel by viewModels<SpielViewModel>()

    private val handler = Handler(Looper.myLooper()!!)

    private lateinit var binding: FragmentSpielBinding

    private val spielLogik: SpielLogik?
        get() = viewModel.spielLogik.value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Den Gameloop benötigen wurd nur einmal. */
        if (spielLogik != null)
            return

        /*
         * Hier wird auf Basis der Parameter das Spielfeld
         * sowie der SpielLogik erzeugt und beides im ViewModel
         * gesichert, damit es überleben kann.
         */

        val spielModus = SpielModus.valueOf(args.spielModus)
        val feldGroesse = FeldGroesse.valueOf(args.feldGroesse)

        val spielfeld = Spielfeld(feldGroesse)

        viewModel.spielfeld.value = spielfeld
        viewModel.spielLogik.value = SpielLogik(spielfeld, spielModus)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentSpielBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.spielfeldView.setGameLoop(spielLogik!!)

        spielLogik?.start(this)
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

        findNavController().navigate(action)
    }

    override fun aktualisiereSpielfeldViewAnzeige() {

        binding.spielfeldView.aktualisiereAnzeige()
    }
}