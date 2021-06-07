package com.geekinasuit.demo.klife

import androidx.compose.desktop.Window
import androidx.compose.desktop.WindowEvents
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.geekinasuit.demo.klife.Resources.PAUSE_BUTTON_ICON
import com.geekinasuit.demo.klife.Resources.PLAY_BUTTON_ICON
import com.google.common.flogger.FluentLogger
import java.lang.Integer.max
import kotlin.concurrent.timerTask
import kotlin.math.min
import kotlin.math.roundToInt

private val logger = FluentLogger.forEnclosingClass()

fun main() {
  Global.newLife()
}

fun newLifeWindow(key: Int, space: BitMatrix? = null, scale: Int? = null) {
  val state = LifeState().also { Global.states[key] = it }
  scale?.let { state.scale.value = it }
  space?.let { state.space.value = it }
  val tickTask = timerTask {
    if (state.active.value) {
      state.space.value = evolve(state.space.value)
    }
  }
  Global.timer.scheduleAtFixedRate(tickTask, 0, 1000L / state.speed.value)
  Window(
    title = "KLife",
    size = IntSize(
      max(space?.width ?: INITIAL_WIDTH, MIN_WIDTH),
      max(space?.height ?: INITIAL_HEIGHT, MIN_HEIGHT),
    ),
    events = WindowEvents(
      onRelocate = {},
      onResize = { size ->
        windowSizeConstrainer.constrainResize(size)
        logger.atInfo().log("Resize event to width: ${size.width}, height: ${size.height}")
      },
      onClose = {
        Global.states.remove(key)
        tickTask.cancel()
      }
    ),
    menuBar = menu(state),
  ) {
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
              onClick = { Actions.toggleActive(state) },
              shape = MaterialTheme.shapes.medium
            ) {
              Image(if (state.active.value) PAUSE_BUTTON_ICON else PLAY_BUTTON_ICON, "play")
            }
            Button(
              onClick = {
                if (!state.active.value) Actions.stepEvolution(state)
              },
              enabled = !state.active.value,
              shape = MaterialTheme.shapes.medium
            ) { Text("Step") }
            Slider(
              value = state.scale.value.toFloat(),
              onValueChange = { state.scale.value = it.roundToInt() },
              valueRange = 7.0f..20.0f,
              modifier = Modifier.width(100.dp).requiredHeight(24.dp)
            )
          }
        }
      ) {
        Canvas(
          modifier = Modifier.fillMaxSize()
            .pointerInput(Unit) {
              detectTapGestures(
                onTap = { offset ->
                  state.space.value =
                    ArrayBitMatrix(state.space.value).apply {
                      val scale = state.scale.value
                      toggle(offset.x.roundToInt() / scale, offset.y.roundToInt() / scale)
                    }
                }
              )
            }
        ) {
          val (width, height) = size
          state.fileToLoad.value?.let { file ->
            logger.atInfo().log("Found $file - loading")
            loadMatrixFromImage(file)?.also { matrix ->
              state.space.value = matrix
            }
            // We read the file (if any), now set the fileToLoad to null again.
            state.fileToLoad.value = null
          }

          val scale = state.scale.value

          val space = with(state.space.value) {
            val newWidth = width.roundToInt() / scale
            val newHeight = height.roundToInt() / scale
            if (this.width != newWidth || this.height != newHeight) {
              ArrayBitMatrix(newWidth, newHeight).also {
                logger.atInfo().log(
                  "Resizing model from (${this.width}, ${this.height}) to ($newWidth, $newHeight)"
                )
                for (x in 0 until min(this.width, newWidth)) {
                  for (y in 0 until min(this.height, newHeight)) {
                    it.set(x, y, this.get(x, y))
                  }
                }
              }.also {
                state.space.value = it
              }
            } else this
          }

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
