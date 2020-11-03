package de.stefan_oltmann.kaesekaestchen.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import de.stefan_oltmann.kaesekaestchen.R
import de.stefan_oltmann.kaesekaestchen.databinding.FragmentScoreboardBinding
import de.stefan_oltmann.kaesekaestchen.model.Spieler

class ScoreBoardFragment : Fragment() {

    private val args: ScoreBoardFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentScoreboardBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        val gewinner = Spieler.valueOf(args.gewinnerSpieler)

        with(binding) {

            if (gewinner == Spieler.KAESE)
                pokalImageView.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_pokal_kaese))

            if (gewinner == Spieler.MAUS)
                pokalImageView.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_pokal_maus))

            punktestandMausText.text = args.punktestandMaus.toString()
            punktestandKaeseText.text = args.punktestandKaese.toString()
        }

        binding.hauptmenueButton.setOnClickListener {

            val action = ScoreBoardFragmentDirections.actionNavGewonnenToNavStart()

            findNavController().navigate(action)
        }

        return binding.root
    }
}