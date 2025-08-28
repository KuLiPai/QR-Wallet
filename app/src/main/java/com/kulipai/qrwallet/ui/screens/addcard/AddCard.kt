package com.kulipai.qrwallet.ui.screens.addcard

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kulipai.qrwallet.R
import com.kulipai.qrwallet.ui.theme.AnimatedNavigation
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AddJNUCardScreenDestination
import com.ramcosta.composedestinations.generated.destinations.AddTextCardScreenDestination
import com.ramcosta.composedestinations.generated.destinations.TestScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun AddCardScreen(
    navigator: DestinationsNavigator,
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            TopAppBar(
                navigationIcon = {
                    IconButton(
                        {
                            navigator.navigateUp()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close_24px),
                            contentDescription = null
                        )
                    }
                },
                title = { },

                )


        }
    ) { innerPadding ->


        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Text(
                    "添加到钱包",
                    style = MaterialTheme.typography.headlineLargeEmphasized,
                    modifier = Modifier.padding(24.dp, 0.dp)
                )
                Spacer(Modifier.height(36.dp))
                CardItem(
                    title = "校园码",
                    subtitle = "江南大学门禁二维码",
                    icon = painterResource(R.drawable.jnu),
                    navigator = navigator,
                    goto = AddJNUCardScreenDestination
                )
//            Spacer(Modifier.height(24.dp))

                CardItem(
                    title = "文本",
                    subtitle = "请输入文本",
                    icon = painterResource(R.drawable.docs_24px),
                    navigator = navigator,
                    goto = AddTextCardScreenDestination
                )
//            Spacer(Modifier.height(24.dp))

                CardItem(
                    title = "复制",
                    subtitle = "just copy,you know?",
                    icon = painterResource(R.drawable.qr_code_scanner_24px),
                    navigator = navigator,
                    goto = TestScreenDestination
                )
            }
        }

    }
}

@Composable
fun CardItem(
    title: String,
    subtitle: String,
    icon: Painter,
    navigator: DestinationsNavigator,
    goto: DirectionDestinationSpec
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                navigator.navigate(goto)
            })
            .padding(24.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


//        Spacer(Modifier.width(24.dp))
        Card(
            Modifier
                .size(48.dp),
            shape = RoundedCornerShape(999.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        ) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    icon,
                    modifier = Modifier.size(26.dp),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
        }
        Spacer(Modifier.width(24.dp))
        Column(
            Modifier.fillMaxHeight()
        ) {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(4.dp))
            Text(
                subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}