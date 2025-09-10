package com.kulipai.qrwallet.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.kulipai.qrwallet.R
import com.kulipai.qrwallet.data.cache.TextCache
import com.kulipai.qrwallet.util.onColor
import com.ramcosta.composedestinations.generated.destinations.TextQRScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextCard(
    navigator: DestinationsNavigator,
    title: String,
    description: String,
    content: String,
    color: String,
    onLongClick: () -> Unit,
) {


    val context = LocalContext.current

    val cardColor = try {
        Color(color.toColorInt())
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(

//        onClick = {
//            TextCache.text = content
//            navigator.navigate(TextQRScreenDestination)
//        },
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = cardColor.onColor()
        ),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp)
            .aspectRatio(1.8f)
            .clip(MaterialTheme.shapes.extraLarge)
            .combinedClickable(
                onClick = {
                    TextCache.text = content
                    navigator.navigate(TextQRScreenDestination)
                },
                onLongClick = onLongClick
            )
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxHeight()

        ) {
            Row {
                Text(
                    title,
                    fontSize = 19.sp
                )
                Spacer(Modifier.weight(1f))
//                Image(
////                    painterResource(R.drawable.jnu),
//                    contentDescription = null,
//                    modifier = Modifier.size(36.dp),
//                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiary)
//
//                )
            }
            Spacer(Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(R.drawable.qr_code_24px),
                    contentDescription = null,
                )
                VerticalDivider(
                    modifier = Modifier
                        .padding(8.dp, 0.dp)
                        .height(24.dp),
                )
                Text(
                    description,
                    fontWeight = FontWeight.Bold,
                    color = cardColor.onColor().copy(alpha = 0.6f)
                )
            }
        }
    }


}