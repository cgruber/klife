package com.geekinasuit.demo.klife

import androidx.compose.runtime.mutableStateOf
import java.io.File

typealias Bit = Boolean
const val ALIVE: Bit = true
const val DEAD: Bit = false

interface BitMatrix {
  val width: Int
  val height: Int
  val strategy: BorderStrategy
  fun get(x: Int, y: Int): Bit
  fun getNeighborhood(x: Int, y: Int): Neighborhood
  fun copy(): BitMatrix
}

interface MutableBitMatrix : BitMatrix {
  fun set(x: Int, y: Int, value: Bit)
  fun toggle(x: Int, y: Int)
  override fun copy(): MutableBitMatrix
}

class ArrayBitMatrix(
  override val width: Int,
  override val height: Int,
  override val strategy: BorderStrategy = ToroidalBorderStrategy
) : MutableBitMatrix {
  constructor(matrix: BitMatrix) : this(matrix.width, matrix.height, matrix.strategy) {
    for (x in 0 until width) {
      for (y in 0 until height) {
        data[x][y] = matrix.get(x, y)
      }
    }
  }
  internal val data = Array(width) { BooleanArray(height) { DEAD } }
  override fun get(x: Int, y: Int): Bit = strategy.get(x, y, this) { x, y -> data[x][y] }
  override fun set(x: Int, y: Int, value: Bit) {
    strategy.set(x, y, value, this) { x, y, value -> data[x][y] = value }
  }

  override fun toggle(x: Int, y: Int) = set(x, y, ! get(x, y))
  override fun getNeighborhood(x: Int, y: Int): Neighborhood {
    return Neighborhood(
      get(x - 1, y - 1), get(x, y - 1), get(x + 1, y - 1),
      get(x - 1, y), get(x, y), get(x + 1, y),
      get(x - 1, y + 1), get(x, y + 1), get(x + 1, y + 1),
    )
  }

  override fun equals(other: Any?): Boolean {
    return when (other) {
      is ArrayBitMatrix -> data.contentDeepEquals(other.data)
      is BitMatrix -> {
        for (x in 0 until width) {
          for (y in 0 until height) {
            if (get(x, y) != other.get(x, y)) return false
          }
        }
        return true
      }
      else -> false
    }
  }

  override fun hashCode() = 31 * data.contentDeepHashCode()

  /** Perform a deep value copy, rather than the default shallow copy from a data class */
  override fun copy() = ArrayBitMatrix(width, height).also { dupe ->
    for (x in 0 until width) {
      for (y in 0 until height) {
        dupe.set(x, y, this.get(x, y))
      }
    }
  }
}

class Neighborhood(
  val upperLeft: Bit,
  val upperCenter: Bit,
  val upperRight: Bit,
  val centerLeft: Bit,
  val center: Bit,
  val centerRight: Bit,
  val lowerLeft: Bit,
  val lowerCenter: Bit,
  val lowerRight: Bit
) {
  val neighbors get() = arrayOf(
    upperLeft, upperCenter, upperRight, centerLeft, centerRight, lowerLeft, lowerCenter, lowerRight
  )
}

interface BorderStrategy {
  fun get(x: Int, y: Int, matrix: BitMatrix, fetcher: (x: Int, y: Int) -> Bit): Bit
  fun set(
    x: Int,
    y: Int,
    value: Bit,
    matrix: BitMatrix,
    setter: (x: Int, y: Int, value: Bit) -> Unit
  )
  fun translateX(x: Int, width: Int): Int
  fun translateY(y: Int, height: Int): Int
}

/**
 * Translates a negative coordinate as coming from the other side of the space, stitching
 * the coordinate space into a Toroid.
 */
object ToroidalBorderStrategy : BorderStrategy {
  override fun get(
    x: Int,
    y: Int,
    space: BitMatrix,
    fetcher: (x: Int, y: Int) -> Bit,
  ): Bit {
    return fetcher(translateX(x, space.width), translateY(y, space.height))
  }

  override fun set(
    x: Int,
    y: Int,
    value: Bit,
    space: BitMatrix,
    setter: (x: Int, y: Int, value: Bit) -> Unit,
  ) {
    setter(translateX(x, space.width), translateY(y, space.height), value)
  }

  override fun translateX(x: Int, width: Int) = shift(x, width)

  override fun translateY(y: Int, height: Int) = shift(y, height)

  internal fun shift(coord: Int, size: Int): Int {
    var xlatn = coord
    while (xlatn !in 0 until size) {
      when {
        xlatn < 0 -> xlatn += size
        xlatn >= size -> xlatn -= size
        else -> throw AssertionError("This should be impossible")
      }
    }
    return xlatn
  }
}

class LifeState {
  val fileToLoad = mutableStateOf<File?>(null)
  val space = mutableStateOf<BitMatrix>(
    ArrayBitMatrix(10, 10).apply {
    }
  )
  val speed = mutableStateOf(8)
  val scale = mutableStateOf(10)
  val active = mutableStateOf(false)
  fun asText(): String {
    with(space.value) {
      val lines = mutableListOf(
        "### Conway's Game of Life save file",
        "size: $width,$height",
        "scale: ${scale.value}"
      )
      for (x in 0 until width) {
        for (y in 0 until height) {
          if (get(x, y)) lines.add("$x,$y")
        }
      }
      return lines.joinToString("\n")
    }
  }
}
