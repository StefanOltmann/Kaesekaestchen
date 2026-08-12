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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.stefan_oltmann.kaesekaestchen.ui.theme.LocalAppColors

private val BUTTON_HORIZONTAL_PADDING = 16.dp

/**
 * The primary action button of the app.
 *
 * It fills with the text color and centers its content, which is drawn in
 * the background color.
 *
 * @param onClick Invoked when the button is pressed.
 * @param modifier The modifier applied to the button.
 * @param content The button content.
 */
@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {

    val colors = LocalAppColors.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(colors.onBackground)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        CompositionLocalProvider(LocalContentColor provides colors.background) {

            Row(
                modifier = Modifier.padding(horizontal = BUTTON_HORIZONTAL_PADDING),
                verticalAlignment = Alignment.CenterVertically
            ) {

                content()
            }
        }
    }
}
