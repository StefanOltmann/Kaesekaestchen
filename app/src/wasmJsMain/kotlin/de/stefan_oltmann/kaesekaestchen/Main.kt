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
package de.stefan_oltmann.kaesekaestchen

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import de.stefan_oltmann.kaesekaestchen.ui.App
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * Web entry point that mounts Compose into the page body.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {

    val body = document.body ?: return

    ComposeViewport(body) {
        hideLoader()
        App()
    }
}

/**
 * Hide the loading screen and reveal the app container.
 */
fun hideLoader() {

    val loader = document.getElementById("loader") as? HTMLElement
    val app = document.getElementById("app") as? HTMLElement

    /* Hide the loader */
    loader?.style?.display = "none"

    /* Show the app */
    app?.style?.display = "block"
}
