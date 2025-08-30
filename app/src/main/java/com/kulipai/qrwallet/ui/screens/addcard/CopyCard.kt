package com.kulipai.qrwallet.ui.screens.addcard


import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kulipai.qrwallet.data.cache.QRScanCache
import com.kulipai.qrwallet.ui.theme.AnimatedNavigation
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AddCardScreenDestination
import com.ramcosta.composedestinations.generated.destinations.AddTextCardScreenDestination
import com.ramcosta.composedestinations.generated.destinations.CopyCardScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.ScannerWithPermissions

@Destination<RootGraph>(style = AnimatedNavigation::class)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CopyCardScreen(
    navigator: DestinationsNavigator
) {

    ScannerWithPermissions(onScanned = {
        QRScanCache.content = it
        navigator.navigate(AddTextCardScreenDestination) {
            popUpTo(CopyCardScreenDestination) { inclusive = true }
        }
        true
    }, types = listOf(CodeType.QR))

}