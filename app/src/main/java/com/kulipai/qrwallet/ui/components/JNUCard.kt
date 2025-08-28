package com.kulipai.qrwallet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kulipai.qrwallet.R
import com.kulipai.qrwallet.util.Prefs
import com.ramcosta.composedestinations.generated.destinations.JNUScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun JNUCard(navigator: DestinationsNavigator) {

    val studentID = Prefs.getString("jnu_param_id").toList().joinToString(" ")
    Card(
        onClick = {
            navigator.navigate(JNUScreenDestination)

        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
//            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp)
            .aspectRatio(1.8f)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxHeight()

        ) {
            Row() {
                Text(
                    "e江南",
                    fontSize = 19.sp
                )
                Spacer(Modifier.weight(1f))
                Image(
                    painterResource(R.drawable.jnu),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiary)

                )
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
                    studentID,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f)
                )
            }
        }
    }
}