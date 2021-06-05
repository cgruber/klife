package com.geekinasuit.demo.klife

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ModelTest {
  private val matrix = ArrayBitMatrix(5, 5, ToroidalBorderStrategy).apply {
    set(1, 3, ALIVE)
    set(2, 4, ALIVE)
    set(2, 0, ALIVE)
    set(4, 2, ALIVE)
  }

  @Test fun testMutableBitMatrixCopyEquality() {
    assertThat(matrix.copy()).isEqualTo(matrix)
  }
  @Test fun testMutableBitMatrixCopyNonIdentity() {
    val actual = matrix.copy()
    assertThat(actual).isNotSameInstanceAs(matrix)
    assertThat(actual.data).isNotSameInstanceAs((matrix.data))
  }

  @Test fun testToroidalBorderStrategyShifting() {
    assertThat(ToroidalBorderStrategy.shift(-7, 5)).isEqualTo(3)
    assertThat(ToroidalBorderStrategy.shift(-1, 5)).isEqualTo(4)
    assertThat(ToroidalBorderStrategy.shift(0, 5)).isEqualTo(0)
    assertThat(ToroidalBorderStrategy.shift(2, 5)).isEqualTo(2)
    assertThat(ToroidalBorderStrategy.shift(4, 5)).isEqualTo(4)
    assertThat(ToroidalBorderStrategy.shift(5, 5)).isEqualTo(0)
    assertThat(ToroidalBorderStrategy.shift(23, 5)).isEqualTo(3)
  }

  @Test fun testToroidalBorderStrategy() {
    assertThat(matrix.get(-1, 2)).isEqualTo(ALIVE)
    assertThat(matrix.get(2, -1)).isEqualTo(ALIVE)
    assertThat(matrix.get(2, -3)).isEqualTo(DEAD)
  }
}
