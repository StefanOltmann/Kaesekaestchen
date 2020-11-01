package de.stefan_oltmann.kaesekaestchen.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import de.stefan_oltmann.kaesekaestchen.R
import de.stefan_oltmann.kaesekaestchen.databinding.FragmentGewonnenBinding
import de.stefan_oltmann.kaesekaestchen.databinding.FragmentSpielBinding

class GewonnenFragment : Fragment() {

    private val args: GewonnenFragmentArgs by navArgs()

    private lateinit var binding: FragmentGewonnenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentGewonnenBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        with(binding) {

            if (args.gewinnerId == 0)
                pokalImageView.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_pokal_kaese))

            if (args.gewinnerId == 1)
                pokalImageView.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_pokal_maus))

            punktestandMausText.text = args.punktestandMaus.toString()
            punktestandKaeseText.text = args.punktestandKaese.toString()
        }

        binding.hauptmenueButton.setOnClickListener {

            val action = GewonnenFragmentDirections.actionNavGewonnenToNavStart()

            NavHostFragment.findNavController(this).navigate(action)
        }

        return binding.root
    }
}