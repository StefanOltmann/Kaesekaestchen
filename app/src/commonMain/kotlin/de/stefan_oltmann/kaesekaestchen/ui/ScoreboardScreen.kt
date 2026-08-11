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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.Res
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.zurueck_zum_hauptmenue
import de.stefan_oltmann.kaesekaestchen.icons.AppIcon
import de.stefan_oltmann.kaesekaestchen.icons.CheeseSymbol
import de.stefan_oltmann.kaesekaestchen.icons.CheeseTrophy
import de.stefan_oltmann.kaesekaestchen.icons.MouseSymbol
import de.stefan_oltmann.kaesekaestchen.icons.MouseTrophy
import de.stefan_oltmann.kaesekaestchen.model.Player
import de.stefan_oltmann.kaesekaestchen.ui.theme.AppTheme
import de.stefan_oltmann.kaesekaestchen.ui.theme.LocalAppColors
import org.jetbrains.compose.resources.stringResource

/* The gap between the two trophies shown on a draw. */
private val TROPHY_GAP = 16.dp

/**
 * The scoreboard shown after the game, with the trophy of the winner,
 * both scores and a button back to the main menu. On a draw, the
 * trophies of both players are shown.
 *
 * @param winner The player with the higher score, or null on a draw.
 * @param cheeseScore The boxes owned by the cheese player.
 * @param mouseScore The boxes owned by the mouse player.
 * @param modifier The modifier applied to the screen.
 * @param onMainMenu Invoked when the user returns to the main menu.
 */
@Composable
fun ScoreboardScreen(
    winner: Player?,
    cheeseScore: Int,
    mouseScore: Int,
    modifier: Modifier = Modifier,
    onMainMenu: () -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (winner == null) {

            /*
             * A draw: both players completed the same number of boxes, so
             * both trophies are shown.
             */
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.borderMargin, vertical = AppTheme.borderMargin),
                contentAlignment = Alignment.Center
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        imageVector = AppIcon.CheeseTrophy,
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(AppTheme.playerSymbolSize)
                    )

                    Spacer(Modifier.width(TROPHY_GAP))

                    Icon(
                        imageVector = AppIcon.MouseTrophy,
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(AppTheme.playerSymbolSize)
                    )
                }
            }

        } else {

            Icon(
                imageVector = if (winner == Player.CHEESE)
                    AppIcon.CheeseTrophy
                else
                    AppIcon.MouseTrophy,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.borderMargin, vertical = AppTheme.borderMargin)
            )
        }

        Row(
            modifier = Modifier.padding(vertical = AppTheme.borderMargin),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = AppIcon.CheeseSymbol,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(AppTheme.playerSymbolSize)
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = cheeseScore.toString(),
                fontSize = AppTheme.textSize
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = "VS",
                fontSize = AppTheme.vsTextSize
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = mouseScore.toString(),
                fontSize = AppTheme.textSize
            )

            Spacer(Modifier.width(16.dp))

            Icon(
                imageVector = AppIcon.MouseSymbol,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(AppTheme.playerSymbolSize)
            )
        }

        AppButton(
            onClick = onMainMenu,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = AppTheme.borderMargin, end = AppTheme.borderMargin, bottom = AppTheme.borderMargin)
                .height(AppTheme.buttonHeight)
        ) {

            Text(
                text = stringResource(Res.string.zurueck_zum_hauptmenue),
                fontSize = AppTheme.textSize
            )
        }
    }
}
