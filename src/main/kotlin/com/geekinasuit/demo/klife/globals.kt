package com.geekinasuit.demo.klife

import androidx.compose.ui.graphics.imageFromResource
import java.util.*

const val INITIAL_WIDTH = 800
const val INITIAL_HEIGHT = 600
const val MIN_WIDTH = 600
const val MIN_HEIGHT = 400
const val MAX_WIDTH = Int.MAX_VALUE
const val MAX_HEIGHT = Int.MAX_VALUE

object Resources {
  val PLAY_BUTTON_ICON = imageFromResource("play-button-icon-200x200-white.png")
  val PAUSE_BUTTON_ICON = imageFromResource("pause-button-icon-200x200-white.png")
}

val windowSizeConstrainer = WindowSizeConstrainer(MIN_WIDTH, MIN_HEIGHT, MAX_WIDTH, MAX_HEIGHT)

object Global {
  val states = mutableMapOf<Int, LifeState>()
  val timer = Timer()
  fun newLife(matrix: BitMatrix? = null, scale: Int? = null) {
    newLifeWindow((states.keys.maxOfOrNull { it } ?: -1) + 1, matrix, scale)
  }
}

object Patterns {
  fun glider(matrix: MutableBitMatrix, x: Int, y: Int) {
    matrix.set(5, 5, ALIVE)
    matrix.set(6, 5, ALIVE)
    matrix.set(7, 5, ALIVE)
    matrix.set(7, 4, ALIVE)
    matrix.set(6, 3, ALIVE)
  }
}
