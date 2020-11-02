package de.stefan_oltmann.kaesekaestchen.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import de.stefan_oltmann.kaesekaestchen.R
import de.stefan_oltmann.kaesekaestchen.databinding.FragmentSpielBinding
import de.stefan_oltmann.kaesekaestchen.model.*
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class SpielFragment : Fragment() {

    companion object {

        private const val AUSGEWAEHLT_ALPHA = 1.0f
        private const val AUSGEGRAUT_ALPHA = 0.1f
    }

    private val args: SpielFragmentArgs by navArgs()

    private val lock = ReentrantLock()
    private val condition : Condition = lock.newCondition()

    private lateinit var spielfeld: Spielfeld

    private val spielerManager = SpielerManager()

    private val handler = Handler(Looper.myLooper()!!)

    /** Diese Variable steuert den Game Loop Thread.  */
    @Volatile
    private var running = true

    private lateinit var binding: FragmentSpielBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentSpielBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        val spielModus = SpielModus.valueOf(args.spielModus)
        val feldGroesse = FeldGroesse.valueOf(args.feldGroesse)

        if (spielModus == SpielModus.EINZELSPIELER)
            spielerManager.bestimmeZufaelligComputerGegner()

        spielfeld = Spielfeld.SpielfeldFactory.generiere(feldGroesse)

        binding.spielfeldView.init(spielfeld, lock, condition)

        startGameLoop()

        return binding.root
    }

    override fun onStop() {
        running = false
        super.onStop()
    }

    private fun startGameLoop() {

        val thread = Thread(GameLoopRunnable())

        thread.start()

        running = true
    }

    private inner class GameLoopRunnable : Runnable {

        override fun run() {

            /* Auswahl des ersten Spielers */
            spielerManager.waehleNaechstenSpielerAus()

            while (!isGameOver()) {

                val spieler = spielerManager.getAktuellerSpieler()

                /*
                 * Der Spieler der gerade nicht dran ist bekommt
                 * eine halb-transparente Anzeige.
                 */
                handler.post {

                    binding.spielKaeseImageView.alpha = if (spieler == Spieler.MAUS) AUSGEGRAUT_ALPHA else AUSGEWAEHLT_ALPHA
                    binding.spielMausImageView.alpha = if (spieler == Spieler.KAESE) AUSGEGRAUT_ALPHA else AUSGEWAEHLT_ALPHA
                }

                var eingabeStrich: Strich?

                if (!spielerManager.isComputerGegner(spieler)) {

                    binding.spielfeldView.resetLetzteEingabe()

                    /*
                     * Der Benutzer muss nun seine Eingabe tätigen. Dieser
                     * Gameloop- Thread soll nun darauf warten.
                     */
                    while (binding.spielfeldView.letzteEingabe.also { eingabeStrich = it } == null) {

                        lock.withLock {
                            condition.await()
                        }
                    }

                } else {

                    try {
                        /* Der Nutzer soll die Aktion der KI sehen. */
                        Thread.sleep(500)
                    } catch (ignore: InterruptedException) {
                        /* Ignorieren. */
                    }

                    eingabeStrich = spielfeld.ermittleGutenStrichFuerComputerZug()
                }

                waehleStrich(eingabeStrich!!)

                /*
                 * Wurde die Activity beendet, dann auch diesen Thread stoppen.
                 * Ohne diese Zeile würde die Activity niemals enden und der
                 * Thread immer weiter laufen, bis Android diesen killt. Wir
                 * wollen aber natürlich nicht negativ auffallen.
                 */
                if (!running)
                    return
            }

            /*
             * Wenn alle Kästchen besetzt sind, ist das Spiel vorbei und der
             * "Game Score" kann angezeigt werden.
             */
            if (isGameOver()) {

                /* Noch eine Sekunde warten, um das Ende zu sehen. */
                Thread.sleep(1000)

                val gewinner = ermittleSpielerMitHoechsterPunktZahl()

                val action = SpielFragmentDirections.actionNavSpielToGewonnenFragment(
                    gewinnerSpieler = gewinner.toString(),
                    punktestandKaese = spielfeld.ermittlePunktzahl(Spieler.KAESE),
                    punktestandMaus = spielfeld.ermittlePunktzahl(Spieler.MAUS)
                )

                NavHostFragment.findNavController(this@SpielFragment).navigate(action)
            }
        }
    }

    private fun waehleStrich(strich: Strich) {

        /* Bereits vergebene Striche können nicht ausgewählt werden. */
        if (strich.besitzer != null)
            return

        val aktuellerSpieler = spielerManager.getAktuellerSpieler()

        val kaestchenKonnteGeschlossenWerden =
            spielfeld.waehleStrich(strich, aktuellerSpieler)

        /*
         * Wenn ein Kästchen geschlossen werden konnte, ist derjenige Spieler
         * noch einmal dran. Konnte er keines schließen, ist der andere Spieler
         * wieder dran.
         */
        if (!kaestchenKonnteGeschlossenWerden)
            spielerManager.waehleNaechstenSpielerAus()

        binding.spielfeldView.aktualisiereAnzeige()
    }

    fun isGameOver(): Boolean {
        return spielfeld.isAlleKaestchenHabenBesitzer()
    }

    fun ermittleSpielerMitHoechsterPunktZahl(): Spieler {

        var gewinner: Spieler? = null
        var maxPunktZahl = 0

        for (spieler in spielerManager.spieler) {

            val punktZahl = spielfeld.ermittlePunktzahl(spieler)

            if (punktZahl > maxPunktZahl) {
                gewinner = spieler
                maxPunktZahl = punktZahl
            }
        }

        return gewinner!!
    }
}