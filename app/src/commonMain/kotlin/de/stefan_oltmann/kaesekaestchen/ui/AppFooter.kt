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
package de.stefan_oltmann.kaesekaestchen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* The homepage opened when tapping the footer. */
private const val AUTHOR_WEBSITE_URL = "https://stefan-oltmann.de"

/* The GitHub sponsorship page opened by the sponsor button. */
private const val SPONSOR_URL = "https://github.com/sponsors/StefanOltmann"

/*
 * The credit line is hard-coded and exempt from the i18n rule:
 * It is a fixed brand credit that is never translated.
 */
private const val MADE_BY_TEXT = "Made by Stefan Oltmann"

/* The height of the footer bar. */
private val FOOTER_HEIGHT = 32.dp

/* The horizontal padding of the footer bar. */
private val FOOTER_HORIZONTAL_PADDING = 2.dp

/* The gap left of the credit text. */
private val FOOTER_LEFT_GAP = 8.dp

/* The font size of the credit text. */
private val FOOTER_TEXT_SIZE = 14.sp

/* The vertical offset that optically centers the credit text. */
private val FOOTER_TEXT_VERTICAL_OFFSET = (-1).dp

/**
 * The bottom bar with the app credit and the sponsor button.
 *
 * The bar is deliberately monochrome and fixed in both themes, faithful to
 * the original app.
 *
 * @param modifier The modifier applied to the footer.
 */
@Composable
fun AppFooter(
    modifier: Modifier = Modifier
) {

    val uriHandler = LocalUriHandler.current

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(FOOTER_HEIGHT)
            .background(Color.Black)
            .fillMaxWidth()
            .padding(horizontal = FOOTER_HORIZONTAL_PADDING)
            .clickable {
                uriHandler.openUri(AUTHOR_WEBSITE_URL)
            }
    ) {

        Spacer(modifier = Modifier.width(FOOTER_LEFT_GAP))

        Text(
            text = MADE_BY_TEXT,
            color = Color.White,
            fontSize = FOOTER_TEXT_SIZE,
            maxLines = 1,
            modifier = Modifier.offset(y = FOOTER_TEXT_VERTICAL_OFFSET)
        )

        Spacer(modifier = Modifier.weight(1F))

        SponsorButton(
            onClick = {
                uriHandler.openUri(SPONSOR_URL)
            }
        )
    }
}
