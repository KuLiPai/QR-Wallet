package com.kulipai.qrwallet.util

import androidx.compose.ui.graphics.Color
import kotlin.math.pow



fun Color.luminance(): Double {
    fun channel(c: Float): Double {
        val v = c / 255.0
        return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
    }
    return 0.2126 * channel(red * 255) +
            0.7152 * channel(green * 255) +
            0.0722 * channel(blue * 255)
}

fun contrastRatio(l1: Double, l2: Double): Double {
    val (light, dark) = if (l1 > l2) l1 to l2 else l2 to l1
    return (light + 0.05) / (dark + 0.05)
}

/** 返回适合做前景的颜色 (通常是黑或白)，保证最优对比度 */
fun Color.onColor(): Color {
    val bgL = luminance()
    val whiteContrast = contrastRatio(bgL, Color.White.luminance())
    val blackContrast = contrastRatio(bgL, Color.Black.luminance())
    return if (whiteContrast >= blackContrast) Color.White else Color.Black
}
