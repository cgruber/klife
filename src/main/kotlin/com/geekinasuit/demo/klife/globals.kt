package com.geekinasuit.demo.klife

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.imageFromResource
import org.jetbrains.skija.Color
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

const val MIN_WIDTH = 600
const val MIN_HEIGHT = 400
const val MAX_WIDTH = Int.MAX_VALUE
const val MAX_HEIGHT = Int.MAX_VALUE

object Resources {
  val PLAY_BUTTON_ICON = imageFromResource("play-button-icon-200x200-white.png")
  val PAUSE_BUTTON_ICON = imageFromResource("pause-button-icon-200x200-white.png")
}

val windowSizeConstrainer = WindowSizeConstrainer(MIN_WIDTH, MIN_HEIGHT, MAX_WIDTH, MAX_HEIGHT)

// This is soooo gross.
object GlobalState {
  val fileToLoad = mutableStateOf<File?>(null)
  val space = mutableStateOf<BitMatrix>(ArrayBitMatrix(50, 30).apply {
    set(5, 5, ALIVE)
    set(6, 5, ALIVE)
    set(7, 5, ALIVE)
    set(7, 4, ALIVE)
    set(6, 3, ALIVE)
  })
  val speed = mutableStateOf(4)
  val scale = mutableStateOf(15)
  val active = mutableStateOf(false)
  val timer = Timer()
}

