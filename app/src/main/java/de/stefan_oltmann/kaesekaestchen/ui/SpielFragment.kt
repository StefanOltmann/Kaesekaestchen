package de.stefan_oltmann.kaesekaestchen.ui

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

        val spielerTyp1 = if (args.spieler1Ki) SpielerTyp.COMPUTER else SpielerTyp.MENSCH
        val spielerTyp2 = if (args.spieler2Ki) SpielerTyp.COMPUTER else SpielerTyp.MENSCH
        val feldGroesseX = args.feldGroesseX
        val feldGroesseY = args.feldGroesseY

        spielfeld = Spielfeld.SpielfeldFactory.generiere(feldGroesseX, feldGroesseY)

        binding.spielfeldView.init(spielfeld, lock, condition)

        spielerManager.addSpieler(
            Spieler(
                0, resources.getString(R.string.spieler_1_name),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_spieler_symbol_kaese)!!,
                ContextCompat.getColor(requireContext(), R.color.spieler_1_farbe),
                spielerTyp1
            )
        )

        spielerManager.addSpieler(
            Spieler(
                1, resources.getString(R.string.spieler_2_name),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_spieler_symbol_maus)!!,
                ContextCompat.getColor(requireContext(), R.color.spieler_2_farbe),
                spielerTyp2
            )
        )

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
                 * Anzeige welcher Spieler dran ist und wieviele Punkt dieser
                 * schon hat.
                 */
                handler.post {

                    binding.aktuellerSpielerSymbolImageView.setImageDrawable(spieler.symbol)
                    binding.punkteAnzeigeTextView.text = ermittlePunktzahl(spieler).toString()
                }

                var eingabeStrich: Strich?

                if (!spieler.isComputerGegner) {

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

                val gewinner = ermittleSpielerMitHoechsterPunktZahl()

                val punkteStandMap = mutableMapOf<Int, Int>()

                for (spieler in spielerManager.spieler)
                    punkteStandMap.put(spieler.id, ermittlePunktzahl(spieler))

                val action = SpielFragmentDirections.actionNavSpielToGewonnenFragment(
                    gewinnerId = gewinner.id,
                    punktestandKaese = punkteStandMap[0]!!,
                    punktestandMaus = punkteStandMap[1]!!
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

            val punktZahl = ermittlePunktzahl(spieler)

            if (punktZahl > maxPunktZahl) {
                gewinner = spieler
                maxPunktZahl = punktZahl
            }
        }

        return gewinner!!
    }

    fun ermittlePunktzahl(spieler: Spieler): Int {

        var punkte = 0

        for (kaestchen in spielfeld.kaestchenListe)
            if (kaestchen.besitzer == spieler)
                punkte++

        return punkte
    }
}