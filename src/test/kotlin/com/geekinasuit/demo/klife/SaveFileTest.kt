package com.geekinasuit.demo.klife

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class SaveFileTest {
  @Test fun testBadHeader() {
    val e = assertThrows(LifeFileParseException::class.java) {
      parseHeader("## Irrelevant Comment")
    }
    assertThat(e).hasMessageThat()
      .isEqualTo("Bad initial comment marker, should start with \"### \"")
  }

  @Test fun testBadHeaderNoSpace() {
    val e = assertThrows(LifeFileParseException::class.java) {
      parseHeader("###IrrelevantComment")
    }
    assertThat(e).hasMessageThat()
      .isEqualTo("Bad initial comment marker, should start with \"### \"")
  }

  @Test fun testHeaderComment() {
    val actual = parseHeader("### Some Comment")
    assertThat(actual).isEqualTo("Some Comment")
  }

  @Test fun testSize() {
    val (x, y) = parseSize("size: 300,200")
    assertThat(x).isEqualTo(300)
    assertThat(y).isEqualTo(200)
  }
  @Test fun testSizeBadHeader() {
    val e = assertThrows(LifeFileParseException::class.java) { parseSize("sibe: 300, 200") }
    assertThat(e).hasMessageThat()
      .isEqualTo("Invalid size line \"sibe: 300, 200\" on line 2")
  }

  @Test fun testSizeTooManyNumbers() {
    val e = assertThrows(LifeFileParseException::class.java) { parseSize("size: 300,400, 200") }
    assertThat(e).hasMessageThat()
      .isEqualTo("Invalid pair of numbers on line 2: 300,400, 200")
  }

  @Test fun testSizeBadNumbers() {
    val e = assertThrows(LifeFileParseException::class.java) { parseSize("size: 300a,200") }
    assertThat(e).hasMessageThat().isEqualTo("Invalid pair of numbers on line 2: 300a,200")
  }

  @Test fun testScale() {
    val scale = parseScale("scale: 10")
    assertThat(scale).isEqualTo(10)
  }

  @Test fun testScaleBadHeader() {
    val e = assertThrows(LifeFileParseException::class.java) { parseScale("scbe: 10") }
    assertThat(e).hasMessageThat().isEqualTo("Invalid scale line \"scbe: 10\" on line 3")
  }

  @Test fun testScaleBadNumbers() {
    val e = assertThrows(LifeFileParseException::class.java) { parseScale("scale: 10a") }
    assertThat(e).hasMessageThat()
      .isEqualTo("Scale line \"scale: 10a\" on line 3 did not contain a valid number")
  }
}
