package com.geekinasuit.demo.klife

import androidx.compose.desktop.Window
import androidx.compose.desktop.WindowEvents
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.common.flogger.FluentLogger
import com.geekinasuit.demo.klife.Resources.PAUSE_BUTTON_ICON
import com.geekinasuit.demo.klife.Resources.PLAY_BUTTON_ICON
import kotlin.concurrent.timerTask
import kotlin.math.min
import kotlin.math.roundToInt

private val logger = FluentLogger.forEnclosingClass()

fun main() {
  GlobalState.timer.scheduleAtFixedRate(timerTask {
    if (GlobalState.active.value) {
      GlobalState.space.value = evolve(GlobalState.space.value)
    }
  }, 0, 1000L / GlobalState.speed.value)
  Window(
    title = "Compose for Desktop",
    size = IntSize(900, 500),
    events = WindowEvents(
      onRelocate = {},
      onResize = { size ->
        windowSizeConstrainer.constrainResize(size)
        logger.atInfo().log("Resize event to width: ${size.width}, height: ${size.height}")
      },
    ),
    menuBar = menu(),
  ) {
    val playPauseImage = remember { mutableStateOf(PLAY_BUTTON_ICON) }
    MaterialTheme {
      Scaffold(
        Modifier.padding(4.dp).fillMaxSize(),
        topBar = {
          TopAppBar(
            Modifier.height(48.dp),
            backgroundColor = MaterialTheme.colors.background,
            contentPadding = PaddingValues(all = 4.dp)
          ) {
            Button(
              onClick = {
                playPauseImage.swapBetween(PLAY_BUTTON_ICON, PAUSE_BUTTON_ICON)
                GlobalState.active.value = !GlobalState.active.value
              },
              shape = MaterialTheme.shapes.medium
            ) { Image(playPauseImage.value, "play") }
            Button(
              onClick = {
                if (!GlobalState.active.value) {
                  GlobalState.space.value = evolve(GlobalState.space.value)
                }
              },
              enabled = !GlobalState.active.value,
              shape = MaterialTheme.shapes.medium
            ) { Text("Step") }
//            Slider(
//              value = GlobalState.scale.value.toFloat(),
//              onValueChange = { GlobalState.scale.value = it.roundToInt() },
//              valueRange = 7.0f..20.0f,
//              modifier = Modifier.requiredHeight(24.dp)
//            )
          }
        }
      ) {
        Canvas(
          modifier = Modifier.fillMaxSize()
            .pointerInput(Unit) {
              detectTapGestures(
                onTap = { offset ->
                  logger.atFine().log("Clicked at $offset")
                  val x = offset.x.roundToInt() / GlobalState.scale.value
                  val y = offset.y.roundToInt() / GlobalState.scale.value
                  logger.atInfo().log("Clicked in space at x: $x y: $y")

                  GlobalState.space.value =
                    ArrayBitMatrix(GlobalState.space.value).apply { toggle(x, y) }
                }
              )
            }
        ) {
          val (width, height) = size
          GlobalState.fileToLoad.value?.let { file ->
            logger.atInfo().log("Found $file - loading")
            loadMatrixFromImage(file, width.roundToInt(), height.roundToInt())?.also { matrix ->
              GlobalState.space.value = matrix
            }
            // We read the file (if any), now set the fileToLoad to null again.
            GlobalState.fileToLoad.value = null
          }

          val scale = GlobalState.scale.value

          if (GlobalState.space.value.width != width.roundToInt() / scale ||
            GlobalState.space.value.height != width.roundToInt() / scale
          ) {
            val old = GlobalState.space.value
            val scale = GlobalState.scale.value
            val newWidth = width.roundToInt() / scale
            val newHeight = height.roundToInt() / scale
            logger.atInfo().log(
              "Resizing model from (${old.width}, ${old.height}) to ($newWidth, $newHeight)"
            )
            GlobalState.space.value = ArrayBitMatrix(newWidth, newHeight).also {
              for (x in 0 until min(old.width, newWidth)) {
                for (y in 0 until min(old.height, newHeight)) {
                  it.set(x, y, old.get(x, y))
                }
              }
            }
          }

          val space = GlobalState.space.value

          drawRect(
            Color.DarkGray,
            Offset(0.05f, 0.05f),
            Size(space.width * scale - 0.1f, space.height * scale - 0.1f),
            style = Stroke(0.3f)
          )
          for (x in 0 until space.width) {
            for (y in 0 until space.height) {
              if (space.get(x, y)) {
                drawRect(
                  Color.Black,
                  Offset((0.1f + x) * scale, (0.1f + y) * scale),
                  Size(0.8f * scale, 0.8f * scale)
                )
              }
            }
          }
        }
      }
    }
  }
}



