package com.kulipai.qrwallet.ui.screens.qrcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.kulipai.qrwallet.data.cache.TextCache
import com.kulipai.qrwallet.ui.theme.AnimatedNavigation
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun TextQRScreen(
    navigator: DestinationsNavigator,
) {
    var qrCode by remember { mutableStateOf("") }
    val qrColor = MaterialTheme.colorScheme.onBackground


    qrCode = TextCache.text

    val qrcodePainter: Painter = rememberQrCodePainter(qrCode) {
        errorCorrectionLevel = QrErrorCorrectionLevel.MediumHigh
        shapes(centralSymmetry = true) {
            ball = QrBallShape.roundCorners(.25f)
            darkPixel = QrPixelShape.roundCorners()
            frame = QrFrameShape.roundCorners(.25f)
        }

        colors {
            dark = QrBrush.solid(qrColor) // 二维码颜色
            frame = QrBrush.solid(qrColor) // 边框颜色
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "QR Code",
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(48.dp))
            Image(
                painter = qrcodePainter,
                contentDescription = "QR Code",
                modifier = Modifier.size(200.dp)
            )
        }
    }
}