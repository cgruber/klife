package com.geekinasuit.demo.klife

import androidx.compose.desktop.AppManager
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.asDesktopBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skija.*
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.plaf.nimbus.NimbusLookAndFeel

class WindowSizeConstrainer(
  val minWidth: Int = 0,
  val minHeight: Int = 0,
  val maxWidth: Int = Int.MAX_VALUE,
  val maxHeight: Int = Int.MAX_VALUE
) {
  var oldSize: IntSize? = null
  var offset: IntOffset? = null
  fun constrainResize(size: IntSize) {
    var dirty = false
    val width = when {
      size.width < minWidth -> minWidth.also { dirty = true }
      size.width > maxWidth -> maxWidth.also { dirty = true }
      else -> size.width
    }
    val height = when {
      size.height < minHeight -> minHeight.also { dirty = true }
      size.height > maxHeight -> maxHeight.also { dirty = true }
      else -> size.height
    }
    if (dirty) {
      // Don't bother setting if it's not below the constraints.

      AppManager.focusedWindow?.apply {
        setSize(width, height)
        oldSize = IntSize(width, height)
      }
    }
  }
}

fun <T> MutableState<T>.swapBetween(first: T, second: T) {
  this.value = when (this.value) {
    first -> second
    second -> first
    else -> throw IllegalArgumentException("Unexpected value for swap: ${this.value}")
  }
}

internal fun loadMatrixFromImage(file: File): BitMatrix? {
  if (!file.exists()) return null
  val imageBytes = file.readBytes()
  val loaded = Image.makeFromEncoded(imageBytes).asImageBitmap().asDesktopBitmap()
  return ArrayBitMatrix(loaded.width, loaded.height).apply {
    for (x in 0..loaded.width) {
      for (y in 0..loaded.height) {
        set(x, y, loaded.getColor(x, y) >= -1)
      }
    }
  }
}

internal fun loadMatrixFromLifeTextFile(file: File): Pair<BitMatrix, Int>? {
  if (!file.exists()) return null
  val lines = ArrayDeque(file.readLines())
  return loadMatrixFromLifeTextLines(lines, file.name)
}

fun chooseFile(): File? {
  try {
    UIManager.setLookAndFeel(NimbusLookAndFeel::class.java.name)
  } catch (e: Exception) { }
  val chooser = JFileChooser().apply {
    dialogTitle = "Open Life File"
    fileFilter = FileNameExtensionFilter(
      "JPG, GIF, and PNG Images, and .life text files.",
      "jpg", "jpeg", "gif", "png", "life"
    )
  }
  val returnVal = chooser.showOpenDialog(null)
  return if (returnVal == JFileChooser.APPROVE_OPTION) {
    chooser.selectedFile
  } else null
}

fun saveFile(): File? {
  try {
    UIManager.setLookAndFeel(NimbusLookAndFeel::class.java.name)
  } catch (e: Exception) { }
  val chooser = JFileChooser().apply {
    dialogTitle = "Save Life File"
  }

  val returnVal = chooser.showSaveDialog(null)
  return if (returnVal == JFileChooser.APPROVE_OPTION) {
    chooser.selectedFile
  } else null
}

val File.suffix get() = this.name.split(".").last()
