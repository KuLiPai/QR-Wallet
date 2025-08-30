package com.kulipai.qrwallet.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kulipai.qrwallet.R
import com.kulipai.qrwallet.data.cache.QRScanCache
import com.kulipai.qrwallet.ui.components.JNUCard
import com.kulipai.qrwallet.ui.components.TextCard
import com.kulipai.qrwallet.ui.theme.AnimatedNavigation
import com.kulipai.qrwallet.util.CardViewModel
import com.kulipai.qrwallet.util.Prefs
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AddCardScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Destination<RootGraph>(style = AnimatedNavigation::class, start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: CardViewModel = hiltViewModel()
) {
    val cards by viewModel.cards.collectAsState()


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
                item {
                    if (Prefs.getString("jnu_param_AUTHTGC", "") != "") {
                        JNUCard(navigator = navigator)
                    }


                }

                items(cards) { card ->
                    TextCard(
                        title = card.title ?: "Nothing",
                        navigator = navigator,
                        description = card.description ?: "Nothing",
                        content = card.content ?: "Nothing",
                        color = card.color ?: "",
                    )
                }
            }


        }
    }
}

