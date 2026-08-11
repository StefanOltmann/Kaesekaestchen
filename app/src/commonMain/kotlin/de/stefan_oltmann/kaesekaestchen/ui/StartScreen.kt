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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.Res
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.board_size_large
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.board_size_medium
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.board_size_small
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.board_size_very_small
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.einzelspieler
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.feld_groesse
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.mehrspieler
import de.stefan_oltmann.kaesekaestchen.app.generated.resources.spielen
import de.stefan_oltmann.kaesekaestchen.data.StartScreenState
import de.stefan_oltmann.kaesekaestchen.icons.AppIcon
import de.stefan_oltmann.kaesekaestchen.icons.Group
import de.stefan_oltmann.kaesekaestchen.icons.Logo
import de.stefan_oltmann.kaesekaestchen.icons.Person
import de.stefan_oltmann.kaesekaestchen.icons.Play
import de.stefan_oltmann.kaesekaestchen.model.BoardSize
import de.stefan_oltmann.kaesekaestchen.model.GameMode
import de.stefan_oltmann.kaesekaestchen.ui.theme.AppTheme
import de.stefan_oltmann.kaesekaestchen.ui.theme.LocalAppColors
import org.jetbrains.compose.resources.stringResource

/**
 * The main menu: game mode, field size and the play button.
 *
 * @param storage The settings storage for the persisted selections.
 * @param modifier The modifier applied to the screen.
 * @param onPlayClick Invoked with the game mode and board size when the
 *   user presses the play button.
 */
@Composable
fun StartScreen(
    storage: Settings,
    modifier: Modifier = Modifier,
    onPlayClick: (GameMode, BoardSize) -> Unit = { _, _ -> }
) {

    val startScreenState = remember { StartScreenState(storage) }

    val colors = LocalAppColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = AppTheme.borderMargin),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            imageVector = AppIcon.Logo,
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = AppTheme.borderMargin, bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            ModeOption(
                symbol = AppIcon.Person,
                label = stringResource(Res.string.einzelspieler),
                selected = startScreenState.gameMode == GameMode.SINGLE_PLAYER,
                onClick = { startScreenState.selectGameMode(GameMode.SINGLE_PLAYER) },
                modifier = Modifier.weight(1f)
            )

            ModeOption(
                symbol = AppIcon.Group,
                label = stringResource(Res.string.mehrspieler),
                selected = startScreenState.gameMode == GameMode.MULTI_PLAYER,
                onClick = { startScreenState.selectGameMode(GameMode.MULTI_PLAYER) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(Res.string.feld_groesse),
                fontSize = AppTheme.textSize
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = boardSizeLabel(startScreenState.boardSize),
                fontSize = AppTheme.textSize
            )
        }

        AppSlider(
            value = startScreenState.boardSize.ordinal,
            maximum = BoardSize.entries.lastIndex,
            onValueChange = { startScreenState.selectBoardSize(BoardSize.entries[it]) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )

        Spacer(Modifier.height(24.dp))

        AppButton(
            onClick = { onPlayClick(startScreenState.gameMode, startScreenState.boardSize) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppTheme.borderMargin)
                .height(AppTheme.buttonHeight)
        ) {

            Icon(
                imageVector = AppIcon.Play,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.size(16.dp))

            Text(
                text = stringResource(Res.string.spielen),
                fontSize = AppTheme.textSize
            )
        }
    }
}

/*
 * One game mode option: the selectable symbol with its label below.
 *
 * The label font is small enough that the label never wraps to a
 * second line.
 */
@Composable
private fun ModeOption(
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

        Spacer(Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = AppTheme.modeLabelTextSize,
            textAlign = TextAlign.Center
        )
    }
}

/*
 * A selectable mode symbol that is greyed out when not selected, with a
 * highlighted chip around the selected one.
 */
@Composable
private fun GameModeButton(
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
            tint = colors.onSurface,
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.chipIconPadding)
                .alpha(if (selected) AppTheme.selectedAlpha else AppTheme.dimmedAlpha)
        )
    }
}

/*
 * The localized name of the given board size.
 */
@Composable
private fun boardSizeLabel(boardSize: BoardSize): String =
    when (boardSize) {

        BoardSize.VERY_SMALL -> stringResource(Res.string.board_size_very_small)
        BoardSize.SMALL -> stringResource(Res.string.board_size_small)
        BoardSize.MEDIUM -> stringResource(Res.string.board_size_medium)
        BoardSize.LARGE -> stringResource(Res.string.board_size_large)
    }
