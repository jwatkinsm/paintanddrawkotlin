package com.example.paintanddrawkotlin

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class LineStroke(
    val points: List<Offset>,
    val color: Color,
    val width: Float
)