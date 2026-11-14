package com.example.paintanddrawkotlin

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.graphics.toArgb

/**
 * Renders the list of drawn [LineStroke] objects onto an off-screen [Bitmap]
 * and saves the resulting image to the device's shared MediaStore gallery under `Pictures/KotlinPaintApp`.
 *
 * @param context The application or activity context used to access ContentResolver and display Toasts.
 * @param strokes The collection of completed [LineStroke]s drawn by the user.
 * @param canvasWidth The width of the drawing surface in pixels.
 * @param canvasHeight The height of the drawing surface in pixels.
 */
fun saveArtworkToGallery(
    context: Context,
    strokes: List<LineStroke>,
    canvasWidth: Int,
    canvasHeight: Int
) {
    // Validate dimensions before creating bitmap
    if (canvasWidth <= 0 || canvasHeight <= 0) return

    // Create an ARGB bitmap and initialize canvas with a white background
    val bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
    val androidCanvas = android.graphics.Canvas(bitmap)
    androidCanvas.drawColor(android.graphics.Color.WHITE)

    // Configure standard Android Paint for vector/stroke rendering
    val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    // Draw each stored stroke segment onto the Android canvas
    for (stroke in strokes) {
        if (stroke.points.size < 2) continue
        paint.color = stroke.color.toArgb()
        paint.strokeWidth = stroke.width

        for (i in 0 until stroke.points.size - 1) {
            val p1 = stroke.points[i]
            val p2 = stroke.points[i + 1]
            androidCanvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
        }
    }

    // Prepare MediaStore content values for saving the image
    val filename = "Artwork_${System.currentTimeMillis()}.png"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/KotlinPaintApp")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    // Write bitmap data to output stream and finalize the MediaStore entry
    uri?.let { imageUri ->
        resolver.openOutputStream(imageUri)?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, contentValues, null, null)
        }
        Toast.makeText(context, "Saved to Pictures/KotlinPaintApp", Toast.LENGTH_SHORT).show()
    }
}
