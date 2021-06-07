package com.geekinasuit.demo.klife

import com.google.common.flogger.FluentLogger
import java.awt.Frame
import javax.swing.JDialog
import javax.swing.JOptionPane

/**
 * A set of actions which can act on state. Each action method must take a State object on which
 * to act.
 */
object Actions {
  private val logger = FluentLogger.forEnclosingClass()

  fun toggleActive(state: LifeState) {
    state.active.value = ! state.active.value
  }

  fun stepEvolution(state: LifeState) {
    state.space.value = evolve(state.space.value)
  }

  fun loadFile() {
    chooseFile()?.let { file ->
      when (file.suffix) {
        "png" -> loadMatrixFromImage(file) to null
        "jpg" -> loadMatrixFromImage(file) to null
        "jpeg" -> loadMatrixFromImage(file) to null
        "gif" -> loadMatrixFromImage(file) to null
        "life" -> loadMatrixFromLifeTextFile(file)
        else -> {
          logger.atSevere().log("Unknown file type (${file.suffix}): $file")
          null
        }
      }?.let { (space, scale) -> Global.newLife(space, scale) }
    }
  }

  fun saveFile(state: LifeState) {
    state.active.value = false // Pause during the save.Ok,
    val dialog = JDialog(null as Frame?, "File Already Exists!!!", true)

    saveFile()?.let { file ->

      var shouldSave = true
      if (file.exists()) {
        val result = JOptionPane.showConfirmDialog(
          null,
          "File already exists, do you want to overwrite?",
          "File already exists!",
          JOptionPane.YES_NO_OPTION
        )
        if (result > 0) shouldSave = false
      }
      if (shouldSave) {
        file.writeText(state.asText())
      }
    }
  }
}
