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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import de.stefan_oltmann.kaesekaestchen.ui.theme.AppTheme
import de.stefan_oltmann.kaesekaestchen.ui.theme.LocalAppColors

/**
 * A selectable mode symbol that is greyed out when not selected, with a
 * highlighted chip around the selected one.
 *
 * @param symbol The mode symbol.
 * @param selected Whether the option is selected.
 * @param onClick Invoked when the option is clicked.
 */
@Composable
internal fun GameModeButton(
    symbol: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    val colors = LocalAppColors.current

    Box(
        modifier = Modifier
            .size(AppTheme.modeButtonSize)
            .clip(RoundedCornerShape(AppTheme.chipCornerRadius))
            .background(if (selected) colors.surface else Color.Transparent)
            .clickable(onClick = onClick)
    ) {

        Icon(
            imageVector = symbol,
            contentDescription = null,
            tint = colors.onBackground,
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.chipIconPadding)
                .alpha(if (selected) AppTheme.selectedAlpha else AppTheme.dimmedAlpha)
        )
    }
}
