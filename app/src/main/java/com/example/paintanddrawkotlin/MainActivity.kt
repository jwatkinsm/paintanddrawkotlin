package com.example.paintanddrawkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

// Custom dark charcoal theme color constants for cohesive UI styling
private val CharcoalBackground = Color(0xFF1E2022)
private val CharcoalSurface = Color(0xFF2A2D32)
private val CharcoalAccent = Color(0xFF526D82)
private val CharcoalHighlight = Color(0xFF9DB2BF)
private val CharcoalText = Color(0xFFF1F5F9)
private val CharcoalColorScheme = darkColorScheme(
    primary = CharcoalAccent,
    onPrimary = CharcoalText,
    secondary = CharcoalHighlight,
    onSecondary = CharcoalBackground,
    background = CharcoalBackground,
    onBackground = CharcoalText,
    surface = CharcoalSurface,
    onSurface = CharcoalText,
    surfaceVariant = CharcoalSurface,
    onSurfaceVariant = CharcoalHighlight,
    outline = Color(0xFF64748B)
)

    class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = CharcoalColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CharcoalBackground
                ) {
                    PaintAppScreen()
                }
            }
        }
    }
}

/**
 * The primary composable screen containing drawing controls, color palettes,
 * size sliders, and the interactive drawing canvas.
 */
@Composable
fun PaintAppScreen() {
    val context = LocalContext.current

    // Core drawing state collections
    val strokes = remember { mutableStateListOf<LineStroke>() }
    val redoStack = remember { mutableStateListOf<LineStroke>() }
    val currentPoints = remember { mutableStateListOf<Offset>() }

    // UI state variables
    var drawTrigger by remember { mutableIntStateOf(0) }
    var isSessionLoaded by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var isEraserActive by remember { mutableStateOf(false) }
    var currentWidth by remember { mutableFloatStateOf(12f) }
    var showColorDialog by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    // Active color evaluates to white (background color) when eraser mode is enabled
    val activeStrokeColor = if (isEraserActive) Color.White else selectedColor

    // Load saved strokes and Undo/Redo history when the app starts
    LaunchedEffect(Unit) {
        val savedSession = loadCanvasSession(context)
        strokes.clear()
        strokes.addAll(savedSession.strokes)
        redoStack.clear()
        redoStack.addAll(savedSession.redoStack)
        isSessionLoaded = true
        drawTrigger++
    }

    // Auto-save whenever a stroke is completed, undone, redone, or cleared
    LaunchedEffect(drawTrigger) {
        if (isSessionLoaded && currentPoints.isEmpty()) {
            saveCanvasSession(context, strokes.toList(), redoStack.toList())
        }
    }

    // Show custom RGB color picker dialog if requested
    if (showColorDialog) {
        CustomColorPickerDialog(
            initialColor = selectedColor,
            onDismiss = { showColorDialog = false },
            onColorSelected = { newColor ->
                selectedColor = newColor
                isEraserActive = false
                showColorDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF36454F))
            .statusBarsPadding()
    ) {
        // Row 1: Action Controls (Undo, Redo, Clear, Save)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo button: pops last stroke into redo stack
            Button(
                onClick = {
                    if (strokes.isNotEmpty()) {
                        redoStack.add(strokes.removeAt(strokes.lastIndex))
                        drawTrigger++
                    }
                },
                enabled = strokes.isNotEmpty(),
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                Text("Undo")
            }

            // Redo button: restores previously undone stroke
            Button(
                onClick = {
                    if (redoStack.isNotEmpty()) {
                        strokes.add(redoStack.removeAt(redoStack.lastIndex))
                        drawTrigger++
                    }
                },
                enabled = redoStack.isNotEmpty(),
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                Text("Redo")
            }

            // Clear button: resets all canvas strokes and redo history
            Button(
                onClick = {
                    strokes.clear()
                    redoStack.clear()
                    drawTrigger++
                },
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                Text("Clear")
            }

            // Save button: exports drawing to device gallery
            Button(
                onClick = {
                    saveArtworkToGallery(context, strokes, canvasSize.width, canvasSize.height)
                },
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                Text("Save")
            }
        }

        // Row 2: Preset Swatches, Custom Color Button, and Eraser Chip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Preset color circular swatches
            val presetColors = listOf(Color.Black, Color.Red, Color.Blue, Color(0xFF16A34A))
            presetColors.forEach { color ->
                val isSelected = !isEraserActive && selectedColor == color
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) Color.DarkGray else Color.LightGray,
                            shape = CircleShape
                        )
                        .clickable {
                            selectedColor = color
                            isEraserActive = false
                        }
                )
            }

            // Custom color picker button
            OutlinedButton(
                onClick = { showColorDialog = true },
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                Text("Custom")
            }

            // Eraser toggle chip
            FilterChip(
                selected = isEraserActive,
                onClick = { isEraserActive = !isEraserActive },
                label = { Text("Eraser") }
            )
        }

        // Row 3: Brush / Eraser Size Slider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isEraserActive) "Eraser: ${currentWidth.toInt()}" else "Brush: ${currentWidth.toInt()}",
                modifier = Modifier.width(85.dp)
            )
            Slider(
                value = currentWidth,
                onValueChange = { currentWidth = it },
                valueRange = 4f..60f,
                modifier = Modifier.weight(1f)
            )
        }

        // Interactive White Drawing Canvas with touch drag gesture recognition
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
                .onSizeChanged { canvasSize = it }
                .pointerInput(activeStrokeColor, currentWidth) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            redoStack.clear() // Clear redo history on new stroke
                            currentPoints.add(offset)
                            drawTrigger++
                        },
                        onDragEnd = {
                            if (currentPoints.isNotEmpty()) {
                                strokes.add(
                                    LineStroke(
                                        points = currentPoints.toList(),
                                        color = activeStrokeColor,
                                        width = currentWidth
                                    )
                                )
                                currentPoints.clear()
                                drawTrigger++
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPoints.add(change.position)
                            drawTrigger++
                        }
                    )
                }
        ) {
            // Trigger read forces recomposition and re-rendering when drawTrigger increments
            drawTrigger.let {
                // Render all completed saved strokes
                for (stroke in strokes) {
                    for (i in 0 until stroke.points.size - 1) {
                        drawLine(
                            color = stroke.color,
                            start = stroke.points[i],
                            end = stroke.points[i + 1],
                            strokeWidth = stroke.width,
                            cap = StrokeCap.Round
                        )
                    }
                }
                // Render the currently active in-progress stroke points
                for (i in 0 until currentPoints.size - 1) {
                    drawLine(
                        color = activeStrokeColor,
                        start = currentPoints[i],
                        end = currentPoints[i + 1],
                        strokeWidth = currentWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}
