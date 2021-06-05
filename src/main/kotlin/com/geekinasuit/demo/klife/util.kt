package com.geekinasuit.demo.klife

import androidx.compose.desktop.AppManager
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asDesktopBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.google.common.flogger.FluentLogger
import org.jetbrains.skija.*
import java.io.File
import kotlin.math.min

private val logger = FluentLogger.forEnclosingClass()

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
  this.value = when(this.value) {
    first -> second
    second -> first
    else -> throw IllegalArgumentException("Unexpected value for swap: ${this.value}")
  }
}


internal fun loadMatrixFromImage(file: File, trimToWidth: Int, trimToHeight: Int): BitMatrix? {
  if (!file.exists()) return null
  val imageBytes = file.readBytes()
  val loaded = Image.makeFromEncoded(imageBytes).asImageBitmap().asDesktopBitmap()
  val width = min(loaded.width, trimToWidth)
  val height = min(loaded.height, trimToHeight)
  val matrix = ArrayBitMatrix(width, height)
  for (x in 0..width) {
    for (y in 0..height) {
      matrix.set(x, y, loaded.getColor(x, y) >= -1)
    }
  }
  return matrix
}


