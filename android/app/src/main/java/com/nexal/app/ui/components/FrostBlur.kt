package com.nexal.app.ui.components

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

private const val FROST_WIDTH = 270
private const val FROST_RADIUS = 11
private const val GRAIN_SIZE = 128

private val frostCache = ConcurrentHashMap<Int, ImageBitmap>()

/** A Gaussian-blurred copy of a photo, made once per photo. Run this off the main thread. */
internal fun frostedCopy(photoId: Int, source: ImageBitmap): ImageBitmap =
    frostCache.getOrPut(photoId) {
        val bitmap = source.asAndroidBitmap()
        val height = (FROST_WIDTH * bitmap.height.toFloat() / bitmap.width).toInt().coerceAtLeast(1)
        val small = Bitmap.createScaledBitmap(bitmap, FROST_WIDTH, height, true)
        val pixels = IntArray(FROST_WIDTH * height)
        small.getPixels(pixels, 0, FROST_WIDTH, 0, 0, FROST_WIDTH, height)
        gaussianBlur(pixels, FROST_WIDTH, height, FROST_RADIUS)
        Bitmap.createBitmap(pixels, FROST_WIDTH, height, Bitmap.Config.ARGB_8888).asImageBitmap()
    }

/** Three box blurs in a row are a close match for a true Gaussian, at a fraction of the cost. */
internal fun gaussianBlur(pixels: IntArray, width: Int, height: Int, radius: Int) {
    val scratch = IntArray(pixels.size)
    repeat(3) {
        boxPass(pixels, scratch, width, height, radius)
        boxPass(scratch, pixels, height, width, radius)
    }
}

/** Blurs each row of [source] and writes it transposed, so a second call covers the columns. */
private fun boxPass(source: IntArray, target: IntArray, width: Int, height: Int, radius: Int) {
    val window = radius * 2 + 1
    for (y in 0 until height) {
        val row = y * width
        var red = 0
        var green = 0
        var blue = 0
        for (i in -radius..radius) {
            val p = source[row + i.coerceIn(0, width - 1)]
            red += (p shr 16) and 0xFF
            green += (p shr 8) and 0xFF
            blue += p and 0xFF
        }
        for (x in 0 until width) {
            target[x * height + y] = (0xFF shl 24) or
                ((red / window) shl 16) or ((green / window) shl 8) or (blue / window)
            val leaving = source[row + (x - radius).coerceIn(0, width - 1)]
            val entering = source[row + (x + radius + 1).coerceIn(0, width - 1)]
            red += ((entering shr 16) and 0xFF) - ((leaving shr 16) and 0xFF)
            green += ((entering shr 8) and 0xFF) - ((leaving shr 8) and 0xFF)
            blue += (entering and 0xFF) - (leaving and 0xFF)
        }
    }
}

/** Fine film grain. Without it a dark, smooth blur breaks into visible colour bands. */
internal val glassGrain: ImageBitmap by lazy {
    val random = Random(7)
    val pixels = IntArray(GRAIN_SIZE * GRAIN_SIZE) {
        val shade = if (random.nextBoolean()) 0xFFFFFF else 0x000000
        (random.nextInt(0, 12) shl 24) or shade
    }
    Bitmap.createBitmap(pixels, GRAIN_SIZE, GRAIN_SIZE, Bitmap.Config.ARGB_8888).asImageBitmap()
}
