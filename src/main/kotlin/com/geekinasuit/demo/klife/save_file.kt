package com.geekinasuit.demo.klife

import com.google.common.flogger.FluentLogger
import java.lang.NumberFormatException

private val logger = FluentLogger.forEnclosingClass()

internal fun loadMatrixFromLifeTextLines(lines: ArrayDeque<String>, fileName: String): Pair<BitMatrix, Int>? {
  logger.atInfo().log("Processing lines: %s", lines.joinToString("\n"))
  return try {
    parseHeader(lines.removeFirst())
    val (width, height) = parseSize(lines.removeFirst())
    logger.atInfo().log("Loading matrix with width: %s height %s", width, height)
    val scale = parseScale(lines.removeFirst())
    ArrayBitMatrix(width, height).apply {
      lines.forEachIndexed { index, line ->
        val (x, y) = line.parseCoordinates(index + 3)
        set(x, y, ALIVE)
      }
    } to scale
  } catch (e: LifeFileParseException) {
    logger.atSevere().log("Parsing error in $fileName: ${e.message}")
    null
  } catch (e: NumberFormatException) {
    logger.atSevere().log("Invalid file format - found non-numeric coordinates in $fileName")
    null
  }
}

private fun String.parseProperty(
  key: String,
  lineNumber: Int,
  delimiter: String = ":",
  err: () -> String = { "Invalid $key line \"$this\" on line $lineNumber" }
): String {
  return this.split(delimiter, limit = 2).also { pair ->
    if (pair.size != 2 || pair[0] != key) {
      throw LifeFileParseException(err())
    }
  }.last()
}

private fun String.parseCoordinates(lineNumber: Int): Pair<Int, Int> {
  return trim().split(",", limit = 2).let {
    if (it.size != 2) {
      throw LifeFileParseException("Invalid pair of numbers on line $lineNumber: ${this.trim()}")
    }
    try {
      it[0].trim().toInt() to it[1].trim().toInt()
    } catch (e: NumberFormatException) {
      throw LifeFileParseException("Invalid pair of numbers on line $lineNumber: ${this.trim()}")
    }
  }
}

internal fun parseHeader(line: String): String {
  return line.parseProperty("###", 2, " ") {
    "Bad initial comment marker, should start with \"### \""
  }
}

internal fun parseSize(line: String): Pair<Int, Int> {
  val size = line.parseProperty("size", 2)
  return size.parseCoordinates(2)
}

internal fun parseScale(line: String): Int {
  return try {
    line.parseProperty("scale", 3).trim().toInt()
  } catch (e: NumberFormatException) {
    throw LifeFileParseException("Scale line \"$line\" on line 3 did not contain a valid number")
  }
}

class LifeFileParseException(message: String) : RuntimeException(message)
