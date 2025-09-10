package com.kulipai.qrwallet.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.kulipai.qrwallet.ui.components.TextCard
import com.kulipai.qrwallet.ui.screens.addcard.parseHexColorOrNull
import com.kulipai.qrwallet.ui.screens.addcard.toHexString
import com.kulipai.qrwallet.ui.theme.AnimatedNavigation
import com.kulipai.qrwallet.util.CardViewModel
import com.kulipai.qrwallet.util.onColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AddCardScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.mhssn.colorpicker.ColorPickerDialog
import io.mhssn.colorpicker.ColorPickerType
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Destination<RootGraph>(style = AnimatedNavigation::class, start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: CardViewModel = hiltViewModel()
) {
    val cards by viewModel.cards.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )


    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var input_name by remember { mutableStateOf("") }
    var input_content by remember { mutableStateOf(QRScanCache.content) }
    var input_description by remember { mutableStateOf("") }
    var input_color by remember { mutableStateOf("#") }
    var card_id by remember { mutableStateOf("") }
    val selectColor = parseHexColorOrNull(input_color) ?: Color.Transparent


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(bottom = 0),
        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    navigator.navigate(AddCardScreenDestination)
                },
                modifier = Modifier
                    .padding(0.dp, 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_24px),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Card")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,

        topBar = {

            TopAppBar(
                title = {
                    Text("Wallet")
                },
            )
        }
    ) { innerPadding ->


        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)

        ) {
            LazyColumn(
                Modifier.fillMaxSize()
            ) {

                items(cards) { card ->
                    TextCard(
                        title = card.title ?: "Nothing",
                        navigator = navigator,
                        description = card.description ?: "Nothing",
                        content = card.content ?: "Nothing",
                        color = card.color ?: "",
                        onLongClick = {
                            showBottomSheet = true
                            input_name = card.title ?: "Nothing"
                            input_description = card.description ?: "Nothing"
                            input_content = card.content ?: "Nothing"
                            input_color = card.color ?: "#"
                            card_id = card.id

                        }
                    )
                }
            }



            if (showBottomSheet) {


                ModalBottomSheet(
                    modifier = Modifier.fillMaxHeight(),
                    sheetState = sheetState,
                    onDismissRequest = { showBottomSheet = false }
                ) {
                    val coroutineScope = rememberCoroutineScope()

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp, 0.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text("Edit Card")
                        Spacer(Modifier.height(16.dp))

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
                                Row {
                                    Text(
                                        input_name,
                                        fontSize = 13.sp
                                    )
                                    Spacer(Modifier.weight(1f))
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

                        Spacer(Modifier.height(12.dp))

                        Button(
                            {
                                viewModel.updateCard(
                                    CardInfo(
                                        id = card_id,
                                        title = input_name,
                                        description = input_description,
                                        content = input_content,
                                        color = input_color,
                                    )
                                )

                                coroutineScope.launch {
                                    sheetState.hide()
                                }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }

                            },
                        ) {
                            Text("Save Changes")
                        }
                        Spacer(Modifier.height(16.dp))

                        Button(
                            {
                                showDeleteDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                        ) {
                            Text("Delete Card")
                        }
                    }
                }
            }

            val colorPickerType = ColorPickerType.Classic(
                showAlphaBar = true
            )




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


            if (showDeleteDialog) {
                val coroutineScope = rememberCoroutineScope()
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text(text = "确认操作")
                    },
                    text = {
                        Text(text = "您确定要删除吗？")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteCard(card_id)

                                coroutineScope.launch {
                                    sheetState.hide()
                                }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                        showDeleteDialog = false

                                    }
                                }


                            }
                        ) {
                            Text("确认")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                            }
                        ) {
                            Text("取消")
                        }
                    }
                )
            }


        }
    }
}

