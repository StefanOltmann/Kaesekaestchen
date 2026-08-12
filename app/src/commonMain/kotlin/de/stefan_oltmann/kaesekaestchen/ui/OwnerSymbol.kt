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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import de.stefan_oltmann.kaesekaestchen.icons.AppIcon
import de.stefan_oltmann.kaesekaestchen.icons.CheeseSymbol
import de.stefan_oltmann.kaesekaestchen.icons.MouseSymbol
import de.stefan_oltmann.kaesekaestchen.model.Box
import de.stefan_oltmann.kaesekaestchen.model.Player

/**
 * The owner symbol of one box, or nothing when the box has no owner yet.
 *
 * @param box The box to render.
 * @param geometry The computed board geometry.
 * @param symbolPaddingPx The padding around the symbol.
 */
@Composable
internal fun OwnerSymbol(
    box: Box,
    geometry: BoardGeometry,
    symbolPaddingPx: Float
) {

    val owner = box.owner ?: return

    val density = LocalDensity.current

    val symbolSize = with(density) { (geometry.sideLength - symbolPaddingPx * 2).toDp() }

    Image(
        imageVector = if (owner == Player.CHEESE) AppIcon.CheeseSymbol else AppIcon.MouseSymbol,
        contentDescription = null,
        modifier = Modifier
            .offset(
                x = with(density) {
                    (box.gridX * geometry.sideLength +
                        geometry.paddingPx + geometry.offsetX + symbolPaddingPx).toDp()
                },
                y = with(density) {
                    (box.gridY * geometry.sideLength +
                        geometry.paddingPx + geometry.offsetY + symbolPaddingPx).toDp()
                }
            )
            .size(symbolSize)
    )
}
