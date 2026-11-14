# Overview

As a software engineer, my goal with this project is to deepen my expertise in modern Android development by mastering declarative UI design with Jetpack Compose, complex state management using Compose state hooks, custom 2D canvas rendering, and secure storage access via the Android MediaStore API. Building this application allowed me to explore how touch pointer events are captured and translated into fluid, responsive vector strokes.

**Paint & Draw** is a lightweight, feature-rich drawing application designed for Android devices. It provides an intuitive digital canvas where users can sketch freehand art, customize colors, adjust brush and eraser widths, manage undo/redo history, and export finished artwork directly to their device gallery.

### How to Use the App:
1. **Drawing**: Tap and drag your finger across the white canvas to draw smooth lines.
2. **Selecting Colors**: Choose from quick preset color swatches (Black, Red, Blue, Green) or tap **Custom** to open the RGB color picker dialog and mix your own color.
3. **Using the Eraser**: Tap the **Eraser** chip to switch to eraser mode, and adjust the size slider to modify the eraser thickness.
4. **Editing (Undo/Redo/Clear)**: Use the **Undo** button to remove the last stroke, **Redo** to restore it, or **Clear** to wipe the canvas clean.
5. **Saving**: Tap **Save** to render your drawing into a PNG image and save it to `Pictures/KotlinPaintApp` in your device gallery.

[Software Demo Video](http://youtube.link.goes.here)

# Development Environment

- **IDE**: Android Studio
- **Build System**: Gradle with Android Gradle Plugin (AGP)
- **Target Platform**: Android (minSdk 24+, targetSdk 34+)
- **Programming Language**: Kotlin
- **UI Framework**: Jetpack Compose & Material 3
- **Graphics & Storage**: AndroidX Compose UI Graphics, Android Graphics (`Canvas`, `Paint`, `Bitmap`), and Android MediaStore API.

# Installation and Running

To install and run the Paint & Draw application on your local development machine or device:

1. **Clone or Download the Repository**:
   Clone the repository to your local machine using Git or download it as a ZIP archive:
   ```bash
   git clone https://github.com/your-username/paintanddrawkotlin.git
   ```

2. **Open in Android Studio**:
   - Launch Android Studio.
   - Select **Open** and navigate to the root directory `paintanddrawkotlin`.
   - Wait for Android Studio to import the project and synchronize Gradle dependencies automatically.

3. **Configure an Android Device / Emulator**:
   - Open the **Device Manager** in Android Studio (`Tools > Device Manager`).
   - Start an existing Android Virtual Device (AVD) running Android 7.0 (API 24) or higher, or connect a physical Android device via USB with **USB Debugging** enabled.

4. **Build and Run**:
   - Select your connected device or emulator from the target dropdown menu in the toolbar.
   - Click the **Run** (`▶`) button or press `Shift + F10` (`Ctrl + R` on macOS) to build and deploy the debug APK onto your device.

# Useful Websites

* [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
* [Android Graphics and Canvas Guide](https://developer.android.com/develop/ui/views/graphics/2d-graphics)
* [Android MediaStore API Guide](https://developer.android.com/training/data-storage/shared/media)

# AI Disclousure

I used AI tools as a learning and reference resource during this project. I used them mainly to brainstorm design ideas, clarify syntax questions, review logic, API data retrieiving and point me toward useful documentation when I got stuck.

When I received suggestions, I did not rely on them blindly. I reviewed the ideas carefully, rewrote code in my own style, adjusted variable and method names to match my project, and made sure the final solution fit naturally into my existing structure.

Using AI in this way helped me better understand programming concepts while still making sure I could explain the code, justify my design choices, and demonstrate my understanding during the video walkthrough.

# Future Work

* [ ] Add shape drawing tools (rectangles, circles, straight lines).
* [ ] Implement layer management for multi-layered artwork composition.
* [ ] Add support for custom brush textures and adjustable opacity.
* [ ] Implement pinch-to-zoom and canvas panning for detailed drawing work.
