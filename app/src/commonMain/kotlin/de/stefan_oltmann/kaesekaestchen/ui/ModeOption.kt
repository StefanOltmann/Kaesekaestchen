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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.stefan_oltmann.kaesekaestchen.ui.theme.AppTheme

/* The gap between the mode symbol and its label. */
private val MODE_LABEL_GAP = 8.dp

/**
 * One game mode option: the selectable symbol with its label below.
 *
 * The label font is small enough that the label never wraps to a
 * second line.
 *
 * @param symbol The mode symbol.
 * @param label The localized label below the symbol.
 * @param selected Whether the option is selected.
 * @param onClick Invoked when the option is clicked.
 * @param modifier The modifier applied to the option.
 */
@Composable
internal fun ModeOption(
    symbol: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GameModeButton(
            symbol = symbol,
            selected = selected,
            onClick = onClick
        )

        Spacer(Modifier.height(MODE_LABEL_GAP))

        Text(
            text = label,
            fontSize = AppTheme.modeLabelTextSize,
            textAlign = TextAlign.Center
        )
    }
}
