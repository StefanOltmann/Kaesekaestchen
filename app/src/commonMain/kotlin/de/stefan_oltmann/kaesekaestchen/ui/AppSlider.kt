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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import de.stefan_oltmann.kaesekaestchen.ui.theme.LocalAppColors
import kotlin.math.max
import kotlin.math.roundToInt

private val SLIDER_HEIGHT = 24.dp
private val TRACK_THICKNESS = 4.dp
private val THUMB_RADIUS = 10.dp
private val THUMB_RING_THICKNESS = 2.dp
private val STEP_DOT_SIZE = 2.dp
private const val STEP_DOT_ALPHA = 0.35f

/**
 * The horizontal span the slider thumb can travel, in pixels.
 */
private fun sliderSpan(totalWidthPx: Float, thumbRadiusPx: Float): Float =
    max(1f, totalWidthPx - 2 * thumbRadiusPx)

/**
 * The step whose center lies nearest to the given touch position.
 */
private fun stepFor(x: Float, span: Float, thumbRadiusPx: Float, maximum: Int): Int {

    val fraction = ((x - thumbRadiusPx) / span).coerceIn(0f, 1f)
    return (fraction * maximum).roundToInt()
}

/**
 * A slider with a fixed number of equally spaced steps.
 *
 * The value snaps to the nearest step while dragging or tapping.
 *
 * @param value The current step, between zero and [maximum].
 * @param maximum The number of the last step.
 * @param onValueChange Invoked with the selected step whenever the slider
 *   changes.
 * @param modifier The modifier applied to the slider.
 */
@Composable
fun AppSlider(
    value: Int,
    maximum: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = LocalAppColors.current

    val currentOnValueChange by rememberUpdatedState(onValueChange)

    val density = LocalDensity.current
    val trackThicknessPx = with(density) { TRACK_THICKNESS.toPx() }
    val thumbRadiusPx = with(density) { THUMB_RADIUS.toPx() }
    val thumbRingThicknessPx = with(density) { THUMB_RING_THICKNESS.toPx() }
    val stepDotSizePx = with(density) { STEP_DOT_SIZE.toPx() }

    Box(
        modifier = modifier
            .height(SLIDER_HEIGHT)
            .pointerInput(maximum, thumbRadiusPx) {

                val span = sliderSpan(size.width.toFloat(), thumbRadiusPx)

                detectTapGestures { offset ->
                    currentOnValueChange(stepFor(offset.x, span, thumbRadiusPx, maximum))
                }
            }
            .pointerInput(maximum, thumbRadiusPx) {

                val span = sliderSpan(size.width.toFloat(), thumbRadiusPx)

                detectDragGestures(
                    onDragStart = { offset ->
                        currentOnValueChange(stepFor(offset.x, span, thumbRadiusPx, maximum))
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        currentOnValueChange(stepFor(change.position.x, span, thumbRadiusPx, maximum))
                    }
                )
            }
    ) {

        Canvas(Modifier.fillMaxSize()) {

            val startX = thumbRadiusPx
            val endX = size.width - thumbRadiusPx
            val trackWidth = max(1f, endX - startX)
            val fraction = if (maximum == 0) 0f else value.toFloat() / maximum
            val thumbX = startX + fraction * trackWidth
            val trackY = size.height / 2f

            drawLine(
                color = colors.surface,
                start = Offset(startX, trackY),
                end = Offset(endX, trackY),
                strokeWidth = trackThicknessPx
            )

            drawLine(
                color = colors.primary,
                start = Offset(startX, trackY),
                end = Offset(thumbX, trackY),
                strokeWidth = trackThicknessPx
            )

            for (step in 0..maximum) {

                val stepX = startX + step.toFloat() / max(1, maximum) * trackWidth

                drawRect(
                    color = colors.onSurface.copy(alpha = STEP_DOT_ALPHA),
                    topLeft = Offset(stepX - stepDotSizePx / 2, trackY - stepDotSizePx / 2),
                    size = Size(stepDotSizePx, stepDotSizePx)
                )
            }

            drawCircle(
                color = colors.primary,
                radius = thumbRadiusPx,
                center = Offset(thumbX, trackY)
            )

            drawCircle(
                color = colors.onSurface,
                radius = thumbRadiusPx - thumbRingThicknessPx / 2,
                center = Offset(thumbX, trackY),
                style = Stroke(width = thumbRingThicknessPx)
            )
        }
    }
}
