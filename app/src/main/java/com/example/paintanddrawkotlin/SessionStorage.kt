package com.example.paintanddrawkotlin

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** Filename used for persisting the drawing canvas session in internal app storage. */
private const val SESSION_FILE_NAME = "canvas_session.json"

/**
 * Data holder representing a saved canvas session state, including active strokes and redo history.
 *
 * @property strokes List of completed drawing strokes.
 * @property redoStack List of strokes available for redo operations.
 */
data class SavedCanvasSession(
    val strokes: List<LineStroke>,
    val redoStack: List<LineStroke>
)

/**
 * Asynchronously serializes and saves the current drawing canvas session (`strokes` and `redoStack`)
 * to a JSON file in internal app storage on the IO dispatcher.
 *
 * @param context The application context used to access internal files directory.
 * @param strokes The collection of active drawing strokes.
 * @param redoStack The collection of redo history strokes.
 */
suspend fun saveCanvasSession(
    context: Context,
    strokes: List<LineStroke>,
    redoStack: List<LineStroke>
) = withContext(Dispatchers.IO) {
    try {
        // Construct root JSON object containing both stroke collections
        val rootJson = JSONObject().apply {
            put("strokes", strokesToJsonArray(strokes))
            put("redoStack", strokesToJsonArray(redoStack))
        }
        val file = File(context.filesDir, SESSION_FILE_NAME)
        file.writeText(rootJson.toString())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Asynchronously loads and parses the saved drawing canvas session from internal storage on the IO dispatcher.
 * Returns an empty session if no save file exists or if parsing fails.
 *
 * @param context The application context used to access internal files directory.
 * @return A [SavedCanvasSession] containing restored strokes and redo stack.
 */
suspend fun loadCanvasSession(context: Context): SavedCanvasSession = withContext(Dispatchers.IO) {
    val file = File(context.filesDir, SESSION_FILE_NAME)
    if (!file.exists()) {
        return@withContext SavedCanvasSession(emptyList(), emptyList())
    }

    return@withContext try {
        val rootJson = JSONObject(file.readText())
        val strokes = jsonArrayToStrokes(rootJson.optJSONArray("strokes"))
        val redoStack = jsonArrayToStrokes(rootJson.optJSONArray("redoStack"))
        SavedCanvasSession(strokes, redoStack)
    } catch (e: Exception) {
        e.printStackTrace()
        SavedCanvasSession(emptyList(), emptyList())
    }
}

/**
 * Converts a list of [LineStroke] objects into a JSON array structure for persistence.
 */
private fun strokesToJsonArray(strokes: List<LineStroke>): JSONArray {
    val array = JSONArray()
    for (stroke in strokes) {
        val pointsArray = JSONArray()
        for (point in stroke.points) {
            val pointObj = JSONObject().apply {
                put("x", point.x.toDouble())
                put("y", point.y.toDouble())
            }
            pointsArray.put(pointObj)
        }
        val strokeObj = JSONObject().apply {
            put("color", stroke.color.toArgb())
            put("width", stroke.width.toDouble())
            put("points", pointsArray)
        }
        array.put(strokeObj)
    }
    return array
}

/**
 * Parses a JSON array back into a list of [LineStroke] objects.
 */
private fun jsonArrayToStrokes(array: JSONArray?): List<LineStroke> {
    if (array == null) return emptyList()
    val result = mutableListOf<LineStroke>()
    for (i in 0 until array.length()) {
        val strokeObj = array.getJSONObject(i)
        val colorInt = strokeObj.getInt("color")
        val widthFloat = strokeObj.getDouble("width").toFloat()
        val pointsArray = strokeObj.getJSONArray("points")

        val points = mutableListOf<Offset>()
        for (j in 0 until pointsArray.length()) {
            val pointObj = pointsArray.getJSONObject(j)
            points.add(
                Offset(
                    x = pointObj.getDouble("x").toFloat(),
                    y = pointObj.getDouble("y").toFloat()
                )
            )
        }
        result.add(LineStroke(points = points, color = Color(colorInt), width = widthFloat))
    }
    return result
}
