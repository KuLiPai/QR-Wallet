package com.kulipai.qrwallet.ui.screens.others

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kulipai.qrwallet.R
import com.kulipai.qrwallet.ui.theme.AnimatedNavigation
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Rotating3DIconWithTrueDepth(
    size: Dp = 80.dp,
    frontColor: Color = Color(0xFFFFD700),
    backColor: Color = Color(0xFFB8860B),
    sideColor: Color = Color(0xFF8B6508),
    thickness: Dp = 8.dp,
    tiltDegrees: Float = 15f,
    rotationDurationMs: Int = 3000
) {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(rotationDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val cameraDistancePx = with(LocalDensity.current) { 24.dp.toPx() } * 100f
    val thicknessPx = with(LocalDensity.current) { thickness.toPx() }

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // 方法1：使用阴影/边框效果模拟厚度
        // 绘制多层图标来创造厚度感
        repeat(3) { layer ->
            val layerOffset = (layer + 1) * (thicknessPx / 4f)
            val layerAlpha = getSideVisibility(angle) * (0.8f - layer * 0.2f)

            if (layerAlpha > 0.1f) {
                Icon(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    tint = sideColor.copy(alpha = layerAlpha),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = angle
                            rotationX = tiltDegrees
                            cameraDistance = cameraDistancePx
                            translationX =
                                layerOffset * cos(Math.toRadians(angle.toDouble())).toFloat()
                            translationY =
                                layerOffset * sin(Math.toRadians(tiltDegrees.toDouble())).toFloat()
                        }
                )
            }
        }

        // 背面图标
        Icon(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            tint = backColor,
            modifier = Modifier
                .fillMaxSize()
                .alpha(getBackFaceAlpha(angle))
                .graphicsLayer {
                    rotationY = angle + 180f
                    rotationX = tiltDegrees
                    cameraDistance = cameraDistancePx
                }
        )

        // 正面图标
        Icon(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            tint = frontColor,
            modifier = Modifier
                .fillMaxSize()
                .alpha(getFrontFaceAlpha(angle))
                .graphicsLayer {
                    rotationY = angle
                    rotationX = tiltDegrees
                    cameraDistance = cameraDistancePx
                }
        )
    }
}

// 计算正面的透明度
private fun getFrontFaceAlpha(angle: Float): Float {
    val normalizedAngle = ((angle % 360f) + 360f) % 360f
    return when {
        normalizedAngle <= 90f -> 1f
        normalizedAngle <= 180f -> 0f
        normalizedAngle <= 270f -> 0f
        else -> 1f
    }
}

// 计算背面的透明度
private fun getBackFaceAlpha(angle: Float): Float {
    val normalizedAngle = ((angle % 360f) + 360f) % 360f
    return when {
        normalizedAngle <= 90f -> 0f
        normalizedAngle <= 180f -> 1f
        normalizedAngle <= 270f -> 1f
        else -> 0f
    }
}

// 计算侧面可见度
private fun getSideVisibility(angle: Float): Float {
    val normalizedAngle = ((angle % 360f) + 360f) % 360f
    return when {
        normalizedAngle <= 45f -> normalizedAngle / 45f
        normalizedAngle <= 135f -> 1f
        normalizedAngle <= 180f -> (180f - normalizedAngle) / 45f
        normalizedAngle <= 225f -> (normalizedAngle - 180f) / 45f
        normalizedAngle <= 315f -> 1f
        else -> (360f - normalizedAngle) / 45f
    }
}

@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun TestScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text("NoThing")
        Rotating3DIconWithTrueDepth(
            size = 240.dp,
            thickness = 8.dp,
            frontColor = Color(0xFFFFD700),
            backColor = Color(0xFFB8860B),
            sideColor = Color(0xFF8B6508),
            tiltDegrees = 20f,
            rotationDurationMs = 4000
        )

    }
}