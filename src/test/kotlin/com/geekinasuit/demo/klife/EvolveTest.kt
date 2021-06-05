package com.geekinasuit.demo.klife

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EvolveTest {
  @Test fun testOverpopulation() {
    assertThat(evolveBit(Neighborhood(
      ALIVE, ALIVE, DEAD,
      ALIVE, ALIVE, DEAD,
      ALIVE, ALIVE, DEAD
    ))).isEqualTo(DEAD)
    assertThat(evolveBit(Neighborhood(
      ALIVE, ALIVE, DEAD,
      DEAD, ALIVE, DEAD,
      ALIVE, ALIVE, DEAD
    ))).isEqualTo(DEAD)
    assertThat(evolveBit(Neighborhood(
      DEAD, ALIVE, DEAD,
      ALIVE, ALIVE, ALIVE,
      DEAD, ALIVE, DEAD
    ))).isEqualTo(DEAD)
    assertThat(evolveBit(Neighborhood(
      DEAD, ALIVE, DEAD,
      ALIVE, DEAD, ALIVE,
      DEAD, ALIVE, DEAD
    ))).isEqualTo(DEAD)
  }
  @Test fun testUnderpopulation() {
    assertThat(evolveBit(Neighborhood(
      DEAD, DEAD, DEAD,
      DEAD, ALIVE, DEAD,
      DEAD, DEAD, DEAD
    ))).isEqualTo(DEAD)
    assertThat(evolveBit(Neighborhood(
      DEAD, DEAD, DEAD,
      DEAD, DEAD, DEAD,
      DEAD, DEAD, DEAD
    ))).isEqualTo(DEAD)
    assertThat(evolveBit(Neighborhood(
      DEAD, DEAD, DEAD,
      DEAD, ALIVE, DEAD,
      DEAD, ALIVE, DEAD
    ))).isEqualTo(DEAD)
    assertThat(evolveBit(Neighborhood(
      DEAD, ALIVE, DEAD,
      DEAD, ALIVE, DEAD,
      DEAD, DEAD, DEAD
    ))).isEqualTo(DEAD)
  }
  @Test fun testSurvival() {
    assertThat(evolveBit(Neighborhood(
      DEAD, ALIVE, DEAD,
      DEAD, ALIVE, DEAD,
      ALIVE, DEAD, DEAD
    ))).isEqualTo(ALIVE)
    assertThat(evolveBit(Neighborhood(
      ALIVE, DEAD, ALIVE,
      DEAD, ALIVE, DEAD,
      DEAD, DEAD, ALIVE
    ))).isEqualTo(ALIVE)
  }

  @Test fun testReproduction() {
    assertThat(evolveBit(Neighborhood(
      DEAD, ALIVE, DEAD,
      DEAD, DEAD, ALIVE,
      ALIVE, DEAD, DEAD
    ))).isEqualTo(ALIVE)
    assertThat(evolveBit(Neighborhood(
      ALIVE, DEAD, ALIVE,
      DEAD, DEAD, DEAD,
      DEAD, DEAD, ALIVE
    ))).isEqualTo(ALIVE)
  }
}
