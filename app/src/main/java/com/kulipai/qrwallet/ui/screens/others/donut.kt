package com.kulipai.qrwallet.ui.screens.others

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun Donut3D(
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    // 圆环参数：R2 是主半径（从中心到管道中心），R1 是管道半径
    R2: Float = 1.0f,
    R1: Float = 0.5f,
    // 透视与缩放
    k2: Float = 3.5f,              // 越大透视越弱，防止除 0
    scale: Float = 130f,           // 投影缩放到屏幕像素（会再乘以 Canvas 尺寸比例）
    // 光照
    ambient: Float = 0.15f,        // 环境光
    lightDir: FloatArray = floatArrayOf(0.0f, 1.0f, -1.0f), // 光照方向
    // 扫描精度（越大越细腻，性能开销上升）
    stepsPhi: Int = 120,           // 环向
    stepsTheta: Int = 48,          // 管向
    // 颜色（会根据亮度做明暗）
    baseColor: Color = Color(0xFF00D3FF),
    // 旋转速度（毫秒转一圈）
    periodZ: Int = 8000,           // 围绕 Z 旋转周期
    periodX: Int = 6000,           // 围绕 X 旋转周期
    pointRadius: Float = 2.2f      // 点半径（像素）
) {
    val inf = rememberInfiniteTransition(label = "donut-anim")
    val a by inf.animateFloat(
        0f, 360f,
        animationSpec = infiniteRepeatable(tween(periodZ, easing = LinearEasing)),
        label = "rotZ"
    )
    val b by inf.animateFloat(
        0f, 360f,
        animationSpec = infiniteRepeatable(tween(periodX, easing = LinearEasing)),
        label = "rotX"
    )

    // 预归一化光照向量
    val L = remember(lightDir) {
        val len =
            sqrt(lightDir[0] * lightDir[0] + lightDir[1] * lightDir[1] + lightDir[2] * lightDir[2])
        floatArrayOf(lightDir[0] / len, lightDir[1] / len, lightDir[2] / len)
    }

    // 预生成角度表，运行期只做旋转 + 投影
    val thetaList =
        remember(stepsTheta) { FloatArray(stepsTheta) { it * (2f * PI.toFloat() / stepsTheta) } }
    val phiList =
        remember(stepsPhi) { FloatArray(stepsPhi) { it * (2f * PI.toFloat() / stepsPhi) } }

    // 旋转的 cos/sin
    val (cz, sz) = cosSinDeg(a)
    val (cx, sx) = cosSinDeg(b)

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            // 把“数学空间”缩放到画布像素（scale 会被再乘一个画布系数）
            val canvasScale = min(size.toPx(), size.toPx()) / 240f
            val S = scale * canvasScale

            // 收集所有点，按深度排序后绘制
            val pts = ArrayList<DonutPoint>(stepsPhi * stepsTheta)

            for (i in 0 until stepsTheta) {
                val th = thetaList[i]
                val cth = cos(th);
                val sth = sin(th)

                for (j in 0 until stepsPhi) {
                    val ph = phiList[j]
                    val cph = cos(ph);
                    val sph = sin(ph)

                    // 原始表面点（未旋转）
                    val circle = R2 + R1 * cth
                    var x = circle * cph
                    var y = circle * sph
                    var z = R1 * sth

                    // 法线（未旋转）：(cosθ cosφ, cosθ sinφ, sinθ)
                    var nx = cth * cph
                    var ny = cth * sph
                    var nz = sth

                    // 先绕 X 旋转，再绕 Z 旋转（点）
                    val y1 = y * cx - z * sx
                    val z1 = y * sx + z * cx
                    val x1 = x
                    val x2 = x1 * cz - y1 * sz
                    val y2 = x1 * sz + y1 * cz
                    val z2 = z1

                    // 旋转法线（同样次序）
                    val ny1 = ny * cx - nz * sx
                    val nz1 = ny * sx + nz * cx
                    val nx1 = nx
                    val nx2 = nx1 * cz - ny1 * sz
                    val ny2 = nx1 * sz + ny1 * cz
                    val nz2 = nz1

                    // 近平面偏置，越大透视越弱
                    val inv = 1f / (k2 + z2)
                    val px = (x2 * inv) * S + size.toPx() / 2f
                    val py = (-y2 * inv) * S + size.toPx() / 2f

                    // Lambert 明暗
                    var lum = nx2 * L[0] + ny2 * L[1] + nz2 * L[2]
                    lum = max(0f, lum) // 只要正半球
                    val brightness = ambient + (1f - ambient) * lum

                    pts += DonutPoint(
                        x = px, y = py,
                        depth = z2,         // 用 z2 排序（Painter 算法：先远后近）
                        brightness = brightness
                    )
                }
            }

            // 远 -> 近 排序，保证近处覆盖远处
            pts.sortBy { it.depth }

            // 画点（小圆）+ 按亮度着色
            for (p in pts) {
                drawPointCircle(p, baseColor, pointRadius)
            }
        }
    }
}

private data class DonutPoint(
    val x: Float,
    val y: Float,
    val depth: Float,
    val brightness: Float
)

private fun DrawScope.drawPointCircle(p: DonutPoint, base: Color, r: Float) {
    // 把亮度映射到颜色：和黑色做插值
    val c = base * p.brightness
    drawCircle(color = c, radius = r, center = Offset(p.x, p.y))
}

// 颜色乘以系数的小工具
private operator fun Color.times(k: Float): Color {
    val rr = (red * k).coerceIn(0f, 1f)
    val gg = (green * k).coerceIn(0f, 1f)
    val bb = (blue * k).coerceIn(0f, 1f)
    return Color(rr, gg, bb, alpha)
}

private fun cosSinDeg(deg: Float): Pair<Float, Float> {
    val r = Math.toRadians(deg.toDouble())
    return cos(r).toFloat() to sin(r).toFloat()
}

@Composable
fun Donut3DEggScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // 你可以根据彩蛋页风格调整参数
        Donut3D(
            size = 260.dp,
            R2 = 1.1f,
            R1 = 0.5f,
            k2 = 4.0f,
            scale = 140f,
            ambient = 0.18f,
            baseColor = Color(0xFFFF7A18), // 橙金色
            stepsPhi = 150,
            stepsTheta = 60,
            periodZ = 7000,
            periodX = 5200,
            pointRadius = 2.0f
        )
    }
}
