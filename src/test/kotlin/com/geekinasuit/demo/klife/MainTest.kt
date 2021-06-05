package com.geekinasuit.demo.klife

import androidx.compose.runtime.mutableStateOf
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class MainTest {
  @Test
  fun `successful a-to-b`() {
    val foo = mutableStateOf( "foo")
    foo.swapBetween("foo", "bar")
    assertEquals("bar", foo.value)
  }

  @Test
  fun `successful b-to-a`() {
    val foo = mutableStateOf( "bar")
    foo.swapBetween("foo", "bar")
    assertEquals("foo", foo.value)
  }

  @Test
  fun `invalid swap`() {
    val error = assertThrows(Exception::class.java) {
      mutableStateOf( "blah").swapBetween("foo", "bar")
    }
    assertEquals("Unexpected value for swap: blah", error.message)
  }
}
