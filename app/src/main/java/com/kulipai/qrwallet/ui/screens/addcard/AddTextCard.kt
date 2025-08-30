package com.kulipai.qrwallet.ui.screens.addcard

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.kulipai.qrwallet.R
import com.kulipai.qrwallet.data.CardInfo
import com.kulipai.qrwallet.data.cache.QRScanCache
import com.kulipai.qrwallet.ui.theme.AnimatedNavigation
import com.kulipai.qrwallet.util.CardViewModel
import com.kulipai.qrwallet.util.onColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.mhssn.colorpicker.ColorPickerDialog
import io.mhssn.colorpicker.ColorPickerType
import kotlin.math.roundToInt


@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalComposeUiApi::class
)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun AddTextCardScreen(
    navigator: DestinationsNavigator,
    viewModel: CardViewModel = hiltViewModel()
) {

    val colorPickerType = ColorPickerType.Classic(
        showAlphaBar = true
    )

    var showDialog by remember { mutableStateOf(false) }

    var input_name by remember { mutableStateOf("") }
    var input_content by remember { mutableStateOf(QRScanCache.content) }
    var input_description by remember { mutableStateOf("") }
    var input_color by remember { mutableStateOf("#") }
    val selectColor = parseHexColorOrNull(input_color) ?: Color.Transparent


    Scaffold(
        modifier = Modifier.fillMaxSize(),

        floatingActionButton = {

            FloatingActionButton(
                onClick = {

                    viewModel.addCard(
                        CardInfo(
                            id = "",
                            title = input_name,
                            description = input_description,
                            content = input_content,
                            color = input_color
                        )
                    )

                    navigator.navigate(HomeScreenDestination) {
                        popUpTo(NavGraphs.root) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .padding(0.dp, 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LoadingIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("OK")
                }

            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        Log.d("", innerPadding.toString())

        LazyColumn(
            Modifier
                .fillMaxSize()
        ) {
            item {
                Column(
                    Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(128.dp))
                    Text(
                        "Card Info",
                        style = MaterialTheme.typography.headlineLargeEmphasized
                    )
                    Spacer(Modifier.height(32.dp))

                    OutlinedTextField(
                        value = input_name,
                        onValueChange = {
                            input_name = it
                        },
                        shape = MaterialTheme.shapes.large,
                        label = {
                            Text(
                                "Name"
                            )
                        },
                    )

                    Spacer(Modifier.height(16.dp))


                    OutlinedTextField(
                        value = input_description,
                        onValueChange = {
                            input_description = it
                        },
                        shape = MaterialTheme.shapes.large,
                        label = {
                            Text(
                                "Description"
                            )
                        },
                    )

                    Spacer(Modifier.height(16.dp))


                    OutlinedTextField(
                        value = input_content,
                        onValueChange = {
                            input_content = it
                        },
                        shape = MaterialTheme.shapes.large,
                        label = {
                            Text(
                                "Content"
                            )
                        },
                    )

                    Spacer(Modifier.height(16.dp))


                    OutlinedTextField(
                        value = input_color,
                        onValueChange = {
                            input_color = it
                        },
                        shape = MaterialTheme.shapes.large,
                        label = {
                            Text(
                                "Color"
                            )
                        },
                    )


                    Spacer(Modifier.height(16.dp))


                    Card(

                        onClick = {
                            showDialog = true
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = selectColor,
                            contentColor = selectColor.onColor()
                        ),
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier
                            .width(360.dp)
                            .padding(24.dp, 8.dp)
                            .aspectRatio(1.8f)


                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxHeight()

                        ) {
                            Row() {
                                Text(
                                    input_name,
                                    fontSize = 13.sp
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
                                    modifier = Modifier.size(16.dp)
                                )
                                VerticalDivider(
                                    modifier = Modifier
                                        .padding(6.dp, 0.dp)
                                        .height(16.dp),
                                )
                                Text(
                                    input_description,
                                    fontWeight = FontWeight.Bold,
                                    color = selectColor.onColor().copy(alpha = 0.6f),
                                    fontSize = 13.sp

                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(128.dp))


                }
            }


        }
        if (showDialog) {


            ColorPickerDialog(
                show = true,
                type = colorPickerType,
                properties = DialogProperties(),
                onDismissRequest = {
                    showDialog = false
                },
                onPickedColor = {
                    showDialog = false
                    input_color = it.toHexString()
                },

                )

        }
    }
}


fun parseHexColorOrNull(input: String): Color? {
    return try {
        if (!input.startsWith("#")) return null
        val hex = input.removePrefix("#")
        when (hex.length) {
            6 -> { // RGB
                val intColor = hex.toLong(16).toInt()
                Color(
                    red = (intColor shr 16 and 0xFF) / 255f,
                    green = (intColor shr 8 and 0xFF) / 255f,
                    blue = (intColor and 0xFF) / 255f,
                    alpha = 1f
                )
            }

            8 -> { // ARGB
                val intColor = hex.toLong(16).toInt()
                Color(
                    alpha = (intColor shr 24 and 0xFF) / 255f,
                    red = (intColor shr 16 and 0xFF) / 255f,
                    green = (intColor shr 8 and 0xFF) / 255f,
                    blue = (intColor and 0xFF) / 255f,
                )
            }

            else -> null
        }
    } catch (e: Exception) {
        null
    }
}


fun Color.toHexString(includeAlpha: Boolean = false): String {
    val r = (red * 255).roundToInt()
    val g = (green * 255).roundToInt()
    val b = (blue * 255).roundToInt()
    val a = (alpha * 255).roundToInt()

    return if (includeAlpha) {
        String.format("#%02X%02X%02X%02X", a, r, g, b) // AARRGGBB
    } else {
        String.format("#%02X%02X%02X", r, g, b)       // RRGGBB
    }
}