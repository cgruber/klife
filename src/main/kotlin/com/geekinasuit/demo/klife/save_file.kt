package com.geekinasuit.demo.klife

import com.google.common.flogger.FluentLogger
import java.lang.NumberFormatException

private val logger = FluentLogger.forEnclosingClass()

internal fun loadMatrixFromLifeTextLines(lines: ArrayDeque<String>): Pair<BitMatrix, Int>? {
  logger.atInfo().log("Processing lines: %s", lines.joinToString("\n"))
  return try {
    parseHeader(lines.removeFirst())
    var size: Pair<Int, Int>? = null
    var scale: Int? = null
    val liveCoordinates = mutableListOf<Pair<Int, Int>>()
    lines.asSequence()
      .map { line -> line.split("#")[0] }
      .forEachIndexed { index, line ->
        when {
          line.trim().startsWith("size") -> {
            if (size != null) {
              throw LifeFileParseException("Size declared more than once.")
            }
            size = parseSize(line, index + 1)
          }
          line.trim().startsWith("scale") -> {
            if (scale != null) {
              throw LifeFileParseException("Scale declared more than once.")
            }
            scale = parseScale(line, index + 1)
          }
          line.isNotBlank() -> {
            liveCoordinates.add(line.parseCoordinates(index + 3))
          }
        }
    }
    when {
      size == null -> throw LifeFileParseException("No size declared.")
      scale == null -> throw LifeFileParseException("No scale declared.")
      else -> {
        ArrayBitMatrix(size!!.first, size!!.second).apply {
          liveCoordinates.forEach { (x, y) -> set(x, y, ALIVE) }
        } to scale!!
      }
    }
  } catch (e: NumberFormatException) {
    throw LifeFileParseException("Invalid file format - found non-numeric coordinates.")
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
  return line.parseProperty("###", 1, " ") {
    "Bad initial comment marker, should start with \"### \""
  }
}

internal fun parseSize(line: String, lineNumber: Int): Pair<Int, Int> {
  val size = line.parseProperty("size", lineNumber)
  return size.parseCoordinates(lineNumber)
}

internal fun parseScale(line: String, lineNumber: Int): Int {
  return try {
    line.parseProperty("scale", lineNumber).trim().toInt()
  } catch (e: NumberFormatException) {
    throw LifeFileParseException(
      "Scale line \"$line\" on line $lineNumber did not contain a valid number"
    )
  }
}

class LifeFileParseException(message: String) : RuntimeException(message)
