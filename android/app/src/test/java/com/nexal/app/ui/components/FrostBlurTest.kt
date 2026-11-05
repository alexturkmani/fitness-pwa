package com.nexal.app.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FrostBlurTest {
    private val black = 0xFF000000.toInt()
    private val white = 0xFFFFFFFF.toInt()

    private fun red(pixel: Int) = (pixel shr 16) and 0xFF

    @Test
    fun `a flat image stays the same`() {
        val grey = 0xFF808080.toInt()
        val pixels = IntArray(40 * 30) { grey }
        gaussianBlur(pixels, 40, 30, 4)
        assertTrue(pixels.all { it == grey })
    }

    @Test
    fun `size and orientation survive on a non-square image`() {
        // Left half white: after blurring, the left edge must still be brighter than the right.
        val width = 60
        val height = 20
        val pixels = IntArray(width * height) { if (it % width < width / 2) white else black }
        gaussianBlur(pixels, width, height, 3)
        assertEquals(width * height, pixels.size)
        assertTrue(red(pixels[0]) > 240)
        assertTrue(red(pixels[width - 1]) < 15)
    }

    @Test
    fun `a hard edge becomes a smooth ramp with no jumps`() {
        val width = 80
        val height = 8
        val pixels = IntArray(width * height) { if (it % width < width / 2) white else black }
        gaussianBlur(pixels, width, height, 6)
        val row = (0 until width).map { red(pixels[3 * width + it]) }
        val biggestStep = row.zipWithNext { a, b -> kotlin.math.abs(a - b) }.max()
        assertTrue("step was $biggestStep", biggestStep < 16)
        assertTrue(row.zipWithNext().all { (a, b) -> a >= b })
    }

    @Test
    fun `pixels stay fully opaque`() {
        val pixels = IntArray(30 * 30) { if (it % 2 == 0) white else black }
        gaussianBlur(pixels, 30, 30, 5)
        assertTrue(pixels.all { (it ushr 24) == 0xFF })
    }
}
