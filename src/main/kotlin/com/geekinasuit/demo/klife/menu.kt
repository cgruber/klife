package com.geekinasuit.demo.klife

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.window.KeyStroke
import androidx.compose.ui.window.Menu
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuItem
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.UnsupportedLookAndFeelException

import javax.swing.UIManager
import javax.swing.plaf.nimbus.NimbusLookAndFeel


fun menu() = MenuBar(
  Menu(
    "KtLife",
    MenuItem(
      "Load...",
      onClick = { GlobalState.fileToLoad.value = loadFile() },
      shortcut = KeyStroke(Key.L)
    )
  )
)

fun loadFile(): File? {
  try {
    UIManager.setLookAndFeel(NimbusLookAndFeel::class.java.name)
  } catch (e: Exception) {
  }
  val chooser = JFileChooser()
  val filter = FileNameExtensionFilter("JPG, GIF, and PNG Images", "jpg", "jpeg", "gif", "png")
  chooser.fileFilter = filter
  val returnVal = chooser.showOpenDialog(null)
  return if (returnVal == JFileChooser.APPROVE_OPTION) {
    chooser.selectedFile
  } else null
}
