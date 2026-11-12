package com.example.paintanddrawkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Canvas UI will go here
                    DrawingCanvas()

                        }
                }
            }
        }
    }
@Composable
fun DrawingCanvas(modifier: Modifier = Modifier) {
    val lines = remember { mutableStateListOf<List<Offset>>() }
    val currentLine = remember { mutableStateListOf<Offset>() }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentLine.add(offset)
                    },
                    onDragEnd = {
                        lines.add(currentLine.toList())
                        currentLine.clear()
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        currentLine.add(change.position)
                    }
                )
            }
    ) {
        for (line in lines) {
            for (i in 0 until line.size - 1) {
                drawLine(
                    color = Color.Black,
                    start = line[i],
                    end = line[i + 1],
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }
        }
        for (i in 0 until currentLine.size - 1) {
            drawLine(
                color = Color.Black,
                start = currentLine[i],
                end = currentLine[i + 1],
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )
        }
    }
}


