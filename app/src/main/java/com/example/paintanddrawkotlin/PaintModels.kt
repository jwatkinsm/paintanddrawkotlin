package com.example.paintanddrawkotlin

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Represents a single continuous stroke drawn on the canvas.
 *
 * @property points List of 2D screen coordinates (`Offset`) making up the stroke path.
 * @property color The ARGB color of the stroke.
 * @property width The stroke line width in pixels.
 */
data class LineStroke(
    val points: List<Offset>,
    val color: Color,
    val width: Float
)