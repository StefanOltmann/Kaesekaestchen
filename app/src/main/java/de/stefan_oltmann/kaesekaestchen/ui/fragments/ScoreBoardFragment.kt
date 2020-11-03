package de.stefan_oltmann.kaesekaestchen.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import de.stefan_oltmann.kaesekaestchen.R
import de.stefan_oltmann.kaesekaestchen.databinding.FragmentScoreboardBinding
import de.stefan_oltmann.kaesekaestchen.model.Spieler

class ScoreBoardFragment : Fragment() {

    private val args: ScoreBoardFragmentArgs by navArgs()

    private val viewModel by viewModels<ScoreBoardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentScoreboardBinding.inflate(inflater, container, false)

        /* Ein Binding sollte den LifeCycle immer kennen. */
        binding.lifecycleOwner = this

        /* Dem Binding das ViewModel zuweisen. */
        binding.viewModel = viewModel

        val gewinner = Spieler.valueOf(args.gewinnerSpieler)

        /* Die Werte aus den Argumenten in das ViewModel übernehmen. */

        viewModel.pokalDrawableResId.value =
            if (gewinner == Spieler.KAESE)
                R.drawable.ic_pokal_kaese
            else
                R.drawable.ic_pokal_maus

        viewModel.punktestandKaese.value = args.punktestandKaese.toString()
        viewModel.punktestandMaus.value = args.punktestandMaus.toString()

        /* Das Binding auffordern sich auf Basis des aktuellen ViewModels zu aktualisieren. */
        binding.executePendingBindings()

        binding.hauptmenueButton.setOnClickListener {

            val action = ScoreBoardFragmentDirections.actionNavGewonnenToNavStart()

            findNavController().navigate(action)
        }

        return binding.root
    }
}