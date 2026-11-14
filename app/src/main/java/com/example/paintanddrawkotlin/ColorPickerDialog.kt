package com.example.paintanddrawkotlin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A dialog allowing the user to pick a custom color using Red, Green, and Blue sliders.
 *
 * @param initialColor The starting color loaded into the RGB sliders when the dialog opens.
 * @param onDismiss Callback invoked when the dialog is dismissed without selecting.
 * @param onColorSelected Callback invoked with the newly chosen [Color] when the user confirms.
 */
@Composable
fun CustomColorPickerDialog(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    // RGB component state values ranging from 0.0f to 1.0f
    var red by remember { mutableFloatStateOf(initialColor.red) }
    var green by remember { mutableFloatStateOf(initialColor.green) }
    var blue by remember { mutableFloatStateOf(initialColor.blue) }

    // Dynamically constructed preview color based on slider values
    val previewColor = Color(red, green, blue)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Custom Brush Color") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Color preview circular swatch
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(previewColor)
                        .border(2.dp, Color.Gray, CircleShape)
                )

                // Red channel control
                Text("Red: ${(red * 255).toInt()}")
                Slider(value = red, onValueChange = { red = it })

                // Green channel control
                Text("Green: ${(green * 255).toInt()}")
                Slider(value = green, onValueChange = { green = it })

                // Blue channel control
                Text("Blue: ${(blue * 255).toInt()}")
                Slider(value = blue, onValueChange = { blue = it })
            }
        },
        confirmButton = {
            Button(onClick = { onColorSelected(previewColor) }) {
                Text("Select")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
