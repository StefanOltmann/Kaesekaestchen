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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
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
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentScoreboardBinding.inflate(inflater, container, false)

        /* Ein Binding sollte den LifeCycle immer kennen. */
        binding.lifecycleOwner = this

        /* Dem Binding das ViewModel zuweisen. */
        binding.viewModel = viewModel

        val gewinner = Spieler.valueOf(args.gewinnerSpieler)

        /* Die Werte aus den Argumenten in das ViewModel übernehmen. */

        /*
         * Achtung: Das Setzen der Drawable über das Binding scheint nicht abwärtskompatibel zu sein.
         * Auf alten Geräten gibt es hier dann eine verpixelte Grafik. Daher dieser Weg.
         */
        binding.pokalImageView.setImageDrawable(
            if (gewinner == Spieler.KAESE)
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_pokal_kaese)
            else
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_pokal_maus)
        )

        viewModel.punktestandKaese.value = args.punktestandKaese.toString()
        viewModel.punktestandMaus.value = args.punktestandMaus.toString()

        /* Das Binding auffordern sich auf Basis des aktuellen ViewModels zu aktualisieren. */
        binding.executePendingBindings()

        binding.hauptmenueButton.setOnClickListener {
            navigateToStartFragment()
        }

        return binding.root
    }

    private fun navigateToStartFragment() {

        val action = ScoreBoardFragmentDirections.actionNavGewonnenToNavStart()

        findNavController().navigate(action)
    }
}
