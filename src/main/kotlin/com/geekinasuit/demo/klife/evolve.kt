package com.geekinasuit.demo.klife

import com.google.common.flogger.FluentLogger

private val logger = FluentLogger.forEnclosingClass()

fun evolve(matrix: BitMatrix): BitMatrix {
  logger.atFine().log("About to evolve.")
  val dupe = ArrayBitMatrix(matrix.width, matrix.height)
  for (x in 0 until matrix.width) {
    for (y in 0 until matrix.height) {
      dupe.set(x, y, evolveBit(matrix.getNeighborhood(x, y)))
    }
  }
  return dupe
}

/** Takes a bit, and its surrounding state, and determines it's next evolution */
fun evolveBit(neighborhood: Neighborhood): Bit {
  return when (neighborhood.neighbors.count { it == ALIVE }) {
    2 -> if (neighborhood.center) ALIVE else DEAD // bare survival
    3 -> ALIVE // survival or reproduction
    else -> DEAD // over or underpopulation
  }
}
