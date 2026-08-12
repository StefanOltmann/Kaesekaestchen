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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.stefan_oltmann.kaesekaestchen.icons.AppIcon
import de.stefan_oltmann.kaesekaestchen.icons.GithubSponsors

/*
 * The label is hard-coded and exempt from the i18n rule: it is
 * the same word in German and English and is never translated.
 */
private const val SPONSOR_LABEL = "Sponsor"

/* The background color of the sponsor button, the dark surface tone. */
private val sponsorBackgroundColor = Color(0xFF333333)

/* The color of the GitHub Sponsors heart. */
private val heartColor = Color(0xFFEA4AAA)

/* The height of the sponsor button. */
private val SPONSOR_BUTTON_HEIGHT = 24.dp

/* The horizontal padding inside the sponsor button. */
private val SPONSOR_BUTTON_HORIZONTAL_PADDING = 8.dp

/* The corner radius of the sponsor button. */
private val SPONSOR_BUTTON_CORNER_RADIUS = 4.dp

/* The gap between the heart and the label. */
private val SPONSOR_CONTENT_GAP = 6.dp

/* The side length of the heart icon. */
private val SPONSOR_ICON_SIZE = 16.dp

/* The font size of the sponsor label. */
private val SPONSOR_TEXT_SIZE = 14.sp

/* The vertical offset that optically centers the sponsor label. */
private val SPONSOR_TEXT_VERTICAL_OFFSET = (-1).dp

/**
 * The sponsor button shown on the right side of the app footer.
 *
 * @param onClick Invoked when the button is pressed.
 * @param modifier The modifier applied to the button.
 */
@Composable
fun SponsorButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SPONSOR_CONTENT_GAP),
        modifier = modifier
            .background(sponsorBackgroundColor, RoundedCornerShape(SPONSOR_BUTTON_CORNER_RADIUS))
            .height(SPONSOR_BUTTON_HEIGHT)
            .padding(horizontal = SPONSOR_BUTTON_HORIZONTAL_PADDING)
            .clickable(onClick = onClick)
    ) {

        Icon(
            imageVector = AppIcon.GithubSponsors,
            contentDescription = null,
            tint = heartColor,
            modifier = Modifier.size(SPONSOR_ICON_SIZE)
        )

        Text(
            text = SPONSOR_LABEL,
            fontSize = SPONSOR_TEXT_SIZE,
            color = Color.White,
            maxLines = 1,
            modifier = Modifier.offset(y = SPONSOR_TEXT_VERTICAL_OFFSET)
        )
    }
}
