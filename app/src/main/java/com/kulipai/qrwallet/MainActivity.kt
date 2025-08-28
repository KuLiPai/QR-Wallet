package com.kulipai.qrwallet

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kulipai.qrwallet.ui.theme.QRWalletTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Hilt 入口点
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        enableEdgeToEdge()

        setContent {

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            QRWalletTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
//                    contentWindowInsets = WindowInsets(bottom = 0),
//                    floatingActionButton = {
//                        val showFAB = currentRoute in listOf(
//                            HomeScreenDestination.route,
//                        )
//                        if (showFAB) {
//                            FloatingActionButton(
//                                onClick = {
////                                navController.navigate(TestScreenDestination.route)
//                                    navController.navigate(AddCardScreenDestination.route)
//                                },
//                                modifier = Modifier
//                                    .padding(0.dp, 16.dp)
//                            ) {
//                                Row(
//                                    modifier = Modifier.padding(16.dp),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Icon(
//                                        painter = painterResource(R.drawable.add_24px),
//                                        contentDescription = null,
//                                        modifier = Modifier.size(24.dp)
//                                    )
//                                    Spacer(modifier = Modifier.width(8.dp))
//                                    Text("Add Card")
//                                }
//                            }
//                        }
//                    },
//                    floatingActionButtonPosition = FabPosition.End,
//
//                    topBar = {
//                        val showTopBar = currentRoute in listOf(
//                            HomeScreenDestination.route,
//                        )
//                        if (showTopBar) {
//                            TopAppBar(
//                                title = {
//                                    Text("Wallet")
//                                },
//                            )
//                        }
//                        if (currentRoute in listOf(
//                                AddCardScreenDestination.route,
//                            )
//                        ) {
//                            TopAppBar(
//                                navigationIcon = {
//                                    IconButton(
//                                        {
//                                            navController.navigateUp()
//                                        }
//                                    ) {
//                                        Icon(
//                                            painter = painterResource(R.drawable.close_24px),
//                                            contentDescription = null
//                                        )
//                                    }
//                                },
//                                title = { },
//
//                                )
//                        }
//
//                        if (currentRoute in listOf(
//                                TextQRScreenDestination.route,
//                                JNUScreenDestination.route
//                            )
//                        ) {
//                            TopAppBar(
//                                title = { },
//                            )
//                        }
//                    }
                ) { innerPadding ->

                    Log.d("111", innerPadding.toString())
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        start = HomeScreenDestination,

                        navController = navController,
                        modifier = Modifier.fillMaxSize(),
//                        modifier = Modifier.padding(innerPadding)
//                        modifier = if (currentRoute in listOf(
//                                AddCardScreenDestination.route,
//                            )
//                        ) Modifier else Modifier.padding(innerPadding)
                    )

                }
            }
//        }
        }
    }
}

