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
    val (x, y) = parseSize("size: 300,200", 2)
    assertThat(x).isEqualTo(300)
    assertThat(y).isEqualTo(200)
  }
  @Test fun testSizeBadHeader() {
    val e = assertThrows(LifeFileParseException::class.java) { parseSize("sibe: 300, 200", 3) }
    assertThat(e).hasMessageThat()
      .isEqualTo("Invalid size line \"sibe: 300, 200\" on line 3")
  }

  @Test fun testSizeTooManyNumbers() {
    val e = assertThrows(LifeFileParseException::class.java) { parseSize("size: 300,400, 200", 2) }
    assertThat(e).hasMessageThat()
      .isEqualTo("Invalid pair of numbers on line 2: 300,400, 200")
  }

  @Test fun testSizeBadNumbers() {
    val e = assertThrows(LifeFileParseException::class.java) { parseSize("size: 300a,200", 4) }
    assertThat(e).hasMessageThat().isEqualTo("Invalid pair of numbers on line 4: 300a,200")
  }

  @Test fun testScale() {
    val scale = parseScale("scale: 10", 2)
    assertThat(scale).isEqualTo(10)
  }

  @Test fun testScaleBadHeader() {
    val e = assertThrows(LifeFileParseException::class.java) { parseScale("scbe: 10", 3) }
    assertThat(e).hasMessageThat().isEqualTo("Invalid scale line \"scbe: 10\" on line 3")
  }

  @Test fun testScaleBadNumbers() {
    val e = assertThrows(LifeFileParseException::class.java) { parseScale("scale: 10a", 6) }
    assertThat(e).hasMessageThat()
      .isEqualTo("Scale line \"scale: 10a\" on line 6 did not contain a valid number")
  }

  private val file1 = """
    ### Header comment
    # foo
    size: 4, 3 # tail comment
    # bar
    
    scale: 6
    0,1
    3, 2 # another comment
    
    """.trimIndent()

  @Test fun testCommentLines() {
    val (space, scale) = loadMatrixFromLifeTextLines(ArrayDeque(file1.lines()))!!
    assertThat(scale).isEqualTo(6)
    assertThat(space.width).isEqualTo(4)
    assertThat(space.height).isEqualTo(3)
    assertThat(space.get(0,0)).isFalse()
    assertThat(space.get(0,1)).isTrue()
    assertThat(space.get(0,2)).isFalse()
    assertThat(space.get(1,0)).isFalse()
    assertThat(space.get(1,1)).isFalse()
    assertThat(space.get(1,2)).isFalse()
    assertThat(space.get(2,0)).isFalse()
    assertThat(space.get(2,1)).isFalse()
    assertThat(space.get(2,2)).isFalse()
    assertThat(space.get(3,0)).isFalse()
    assertThat(space.get(3,1)).isFalse()
    assertThat(space.get(3,2)).isTrue()
  }

  private val file2 = """
    ### Header comment
    size: 4, 3 # tail comment
    size: 3, 3
    """.trimIndent()

  @Test fun testDuplicateSizeProperty() {
    val e = assertThrows(LifeFileParseException::class.java) {
      loadMatrixFromLifeTextLines(ArrayDeque(file2.lines()))
    }
    assertThat(e).hasMessageThat().contains("Size declared more than once.")
  }
  private val file3 = """
    ### Header comment
    scale: 2
    scale: 3
    """.trimIndent()

  @Test fun testDuplicateScaleProperty() {
    val e = assertThrows(LifeFileParseException::class.java) {
      loadMatrixFromLifeTextLines(ArrayDeque(file3.lines()))
    }
    assertThat(e).hasMessageThat().contains("Scale declared more than once.")
  }

}
