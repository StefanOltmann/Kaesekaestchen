/*
 * Kaesekaestchen
 * A simple Dots'n'Boxes Game for Android
 *
 * Copyright (C) Stefan Oltmann
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
package de.stefan_oltmann.kaesekaestchen.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.stefan_oltmann.kaesekaestchen.SettingsProvider
import de.stefan_oltmann.kaesekaestchen.ui.App

/**
 * Android entry activity that hosts the Compose UI.
 */
class MainActivity : ComponentActivity() {

    /**
     * Initialize the settings storage and render the app content.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        /*
         * The preference file keeps the name and keys of the original app,
         * so that existing players keep their settings after the update.
         */
        val settingsSharedPreferences = getSharedPreferences("game_settings", MODE_PRIVATE)
        SettingsProvider.init(settingsSharedPreferences)

        setContent {

            /*
             * On newer Android versions we need the proper paddings.
             */
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {

                App()
            }
        }
    }
}
