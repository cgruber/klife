package com.geekinasuit.demo.klife

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.window.KeyStroke
import androidx.compose.ui.window.Menu
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuItem

fun menu(state: LifeState) = MenuBar(
  Menu(
    "File",
    MenuItem(
      "New",
      onClick = { Global.newLife() },
      shortcut = KeyStroke(Key.N)
    ),
    MenuItem(
      "Load...",
      onClick = { Actions.loadFile() },
      shortcut = KeyStroke(Key.L)
    ),
    MenuItem(
      "Save...",
      onClick = { Actions.saveFile(state) },
      shortcut = KeyStroke(Key.S)
    )
  ),
  Menu(
    "Control",
    MenuItem(
      if (state.active.value) "Pause" else "Play",
      onClick = { Actions.toggleActive(state) },
      shortcut = KeyStroke(Key.Spacebar)
    ),
    MenuItem(
      "Step",
      onClick = { Actions.stepEvolution(state) },
      shortcut = KeyStroke(Key.Spacebar)
    )
  )
)
