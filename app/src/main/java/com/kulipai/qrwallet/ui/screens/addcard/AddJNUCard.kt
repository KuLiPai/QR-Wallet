package com.kulipai.qrwallet.ui.screens.addcard

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kulipai.qrwallet.R
import com.kulipai.qrwallet.ui.theme.AnimatedNavigation
import com.kulipai.qrwallet.util.ShellManager
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.JNUVerifyScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rikka.shizuku.Shizuku


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun AddJNUCardScreen(
    navigator: DestinationsNavigator,
//    viewModel: JNUCardViewModel = hiltViewModel()

) {

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var useShizuku by remember { mutableStateOf(false) }

    val ok_color = MaterialTheme.colorScheme.primary
    val default_color = MaterialTheme.colorScheme.outlineVariant

    val shizuku_ok = remember { mutableStateOf(false) }

    val param_route = remember { mutableStateOf("") }
//    val param_iPlanetDirectoryPro = remember { mutableStateOf("") }
    val param_AUTHTGC = remember { mutableStateOf("") }

    var error_input by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
//        contentWindowInsets = WindowInsets(bottom = 0),

        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    if (param_AUTHTGC.value.isEmpty() || param_route.value.isEmpty()) {
                        error_input = "error"
                    } else {
//
                        JNUCardCache.param_AUTHTGC = param_AUTHTGC.value
                        JNUCardCache.param_route = param_route.value
//                        JNUCardCache.param_iPlanetDirectoryPro = param_iPlanetDirectoryPro.value
                        navigator.navigate(JNUVerifyScreenDestination)
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
                    Text("Verify")
                }

            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Text(
                    "Plan 1 (ROOT)",
                    style = MaterialTheme.typography.headlineLargeEmphasized,
                    modifier = Modifier.padding(24.dp, 24.dp)
                )

                Text(
                    "确保安装并登录e江南\n" +
                            "连接Shizuku",
                    Modifier.padding(24.dp, 0.dp)
                )

                OutlinedCard(
                    onClick = {
                        useShizuku = true
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    border = BorderStroke(
                        1.618.dp,
                        if (shizuku_ok.value) ok_color else default_color
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(R.drawable.shizuku_logo),
                            contentDescription = null,
                            Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "点我授权",
                            style = MaterialTheme.typography.bodyLarge,
                        )

                    }
                }

                Spacer(Modifier.width(4.dp))

            }

            item {


                Column(
                    modifier = Modifier
                        .padding(0.dp)
                        .padding(24.dp, 24.dp)

                ) {

                    Text(
                        "Plan 2",
                        style = MaterialTheme.typography.headlineLargeEmphasized,
                    )

                    Spacer(Modifier.height(16.dp))

                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        isError = error_input.isNotEmpty(),
                        supportingText = {
                            Text(
                                error_input
                            )
                        },
                        value = param_AUTHTGC.value,
                        onValueChange = {
                            error_input = ""
                            param_AUTHTGC.value = it
                        },
                        shape = MaterialTheme.shapes.large,
                        label = {
                            Text(
                                "AUTHTGC"
                            )
                        },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            val image = if (passwordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            }
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        }
                    )

                    OutlinedTextField(
                        isError = error_input.isNotEmpty(),
                        supportingText = {
                            Text(
                                error_input
                            )
                        },
                        value = param_route.value,
                        onValueChange = {
                            error_input = ""
                            param_route.value = it
                        },
                        shape = MaterialTheme.shapes.large,
                        label = {
                            Text(
                                "route"
                            )
                        },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            val image = if (passwordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            }
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        }
                    )

//                    OutlinedTextField(
//                        isError = error_input.isNotEmpty(),
//                        supportingText = {
//                            Text(
//                                error_input
//                            )
//                        },
//                        value = param_iPlanetDirectoryPro.value,
//                        onValueChange = {
//                            error_input = ""
//                            param_iPlanetDirectoryPro.value = it
//                        },
//                        shape = MaterialTheme.shapes.large,
//                        label = {
//                            Text(
//                                "iPlanetDirectoryPro"
//                            )
//                        },
//                        visualTransformation = if (passwordVisible) {
//                            VisualTransformation.None
//                        } else {
//                            PasswordVisualTransformation()
//                        },
//                        trailingIcon = {
//                            val image = if (passwordVisible) {
//                                Icons.Default.Visibility
//                            } else {
//                                Icons.Default.VisibilityOff
//                            }
//                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                                Icon(imageVector = image, contentDescription = null)
//                            }
//                        }
//                    )



                    Spacer(Modifier.height(16.dp))

                    Text(
                        "使用Reqable等工具对e江南抓包" +
                                "\n打开抓包软件，进入e江南，并进入校园码，成功加载出一个校园码后停止抓包" +
                                "\n找到https://authserver.jiangnan.edu.cn/authserver/oauth2.0/authorize的URL" +
                                "\n复制其中发包过程中使用的Cookie中的route、AUTHTGC参数的值",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "authserver.jiangnan.edu.cn...",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium
                        )
                        TextButton({
                            clipboardManager.setText(AnnotatedString("https://authserver.jiangnan.edu.cn/authserver/oauth2.0/authorize"))
                        }) {
                            Icon(
                                painterResource(R.drawable.content_copy_24px),
                                contentDescription = null
                            )
                        }


                    }

                    Spacer(Modifier.height(256.dp))


                }


            }
        }
    }

    if (useShizuku) {
        UseShizuku(
            shizuku_ok,
//            param_iPlanetDirectoryPro,
            param_route,
            param_AUTHTGC
        )
    }
}


@Composable
fun UseShizuku(
    shizuku_ok: MutableState<Boolean>,
//    param_iPlanetDirectoryPro: MutableState<String>,
    param_route: MutableState<String>,
    param_AUTHTGC: MutableState<String>,
) {
    val context = LocalContext.current

    ShizukuPermissionManager(
        onPermissionGranted = {
            // 权限授予后初始化你的 Shell 或其他操作
            try {
                Toast.makeText(context, "Shizuku 初始化成功", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "初始化失败: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    ) { permissionState, requestPermission ->

        // 根据权限状态显示不同的 UI
        when (permissionState) {
            ShizukuPermissionState.GRANTED -> {
                // 获取成功
                ShellManager.init(context) {
                    shizuku_ok.value = true
                    val (text, isok) = ShellManager.shell("cat /data/user/0/com.wisedu.cpdaily.jiangnan/shared_prefs/private-cookies.xml")
                    if (isok) {
                        val regex1 = Regex("AUTHTGC=(.*?)[;<]")
                        param_AUTHTGC.value = regex1.find(text)?.groupValues?.get(1).toString()

                        val regex2 = Regex("route=(.*?)[;<]")
                        param_route.value = regex2.find(text)?.groupValues?.get(1).toString()

//                        val regex3 = Regex("iPlanetDirectoryPro=(.*?)[;<]")
//                        param_iPlanetDirectoryPro.value =
//                            regex3.find(text)?.groupValues?.get(1).toString()

                    } else {
                        Toast.makeText(context, "读取Cookie失败: $text", Toast.LENGTH_LONG).show()
                    }

                }
            }

            else -> {
                // 显示权限请求界面
                ShizukuPermissionScreen(
                    permissionState = permissionState,
                    onRequestPermission = requestPermission
                )
            }
        }
    }


}


@Composable
fun ShizukuPermissionScreen(
    permissionState: ShizukuPermissionState,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (permissionState) {
            ShizukuPermissionState.CHECKING -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("检查 Shizuku 权限状态...")
            }

            ShizukuPermissionState.UNAVAILABLE -> {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Shizuku 服务未运行")
                Text("请启动 Shizuku 应用或使用 ADB 启动")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRequestPermission) {
                    Text("重新检查")
                }
            }

            ShizukuPermissionState.DENIED -> {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("需要 Shizuku 权限")
                Text("此操作需要 Shizuku 权限才能正常运行")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRequestPermission) {
                    Text("请求权限")
                }
            }

            ShizukuPermissionState.VERSION_TOO_LOW -> {
                Icon(
                    imageVector = Icons.Default.Update,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Shizuku 版本过低")
                Text("请升级到最新版本的 Shizuku")
            }

            ShizukuPermissionState.GRANTED -> {
                // 这种情况下不应该显示这个界面
            }
        }
    }
}


/**
 * Shizuku 权限状态
 */
enum class ShizukuPermissionState {
    CHECKING,           // 检查中
    UNAVAILABLE,        // Shizuku 服务未运行
    DENIED,             // 权限被拒绝
    GRANTED,            // 权限已授予
    VERSION_TOO_LOW     // 版本过低
}

/**
 * Shizuku 权限管理 ViewModel
 */
class ShizukuPermissionViewModel : ViewModel() {

    private val _permissionState = MutableStateFlow(ShizukuPermissionState.CHECKING)
    val permissionState: StateFlow<ShizukuPermissionState> = _permissionState.asStateFlow()

    private val shizukuRequestCode = 100

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        updatePermissionStatus()
    }

    private val requestPermissionResultListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == shizukuRequestCode) {
                val granted = grantResult == PackageManager.PERMISSION_GRANTED
                _permissionState.value = if (granted) {
                    ShizukuPermissionState.GRANTED
                } else {
                    ShizukuPermissionState.DENIED
                }
            }
        }

    fun initialize() {
        Shizuku.addBinderReceivedListener(binderReceivedListener)
        Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
        updatePermissionStatus()
    }

    fun cleanup() {
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
    }

    private fun isShizukuAvailable(): Boolean {
        return try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            false
        }
    }

    private fun checkShizukuPermission(): Boolean {
        return if (Shizuku.isPreV11()) {
            false
        } else {
            try {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            } catch (e: Exception) {
                false
            }
        }
    }

    fun requestShizukuPermission(context: Context) {
        when {
            !isShizukuAvailable() -> {
                _permissionState.value = ShizukuPermissionState.UNAVAILABLE
                Toast.makeText(context, "Shizuku 服务未运行", Toast.LENGTH_SHORT).show()
            }

            Shizuku.isPreV11() -> {
                _permissionState.value = ShizukuPermissionState.VERSION_TOO_LOW
                Toast.makeText(context, "Shizuku 版本过低，请使用 ADB 启动", Toast.LENGTH_LONG)
                    .show()
            }

            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED -> {
                _permissionState.value = ShizukuPermissionState.GRANTED
                Toast.makeText(context, "Shizuku 权限已存在", Toast.LENGTH_SHORT).show()
            }

            else -> {
                try {
                    Shizuku.requestPermission(shizukuRequestCode)
                } catch (e: Exception) {
                    _permissionState.value = ShizukuPermissionState.DENIED
                    Toast.makeText(
                        context,
                        "请求 Shizuku 权限失败: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun updatePermissionStatus() {
        _permissionState.value = when {
            !isShizukuAvailable() -> ShizukuPermissionState.UNAVAILABLE
            Shizuku.isPreV11() -> ShizukuPermissionState.VERSION_TOO_LOW
            checkShizukuPermission() -> ShizukuPermissionState.GRANTED
            else -> ShizukuPermissionState.DENIED
        }
    }
}

/**
 * Composable 函数：Shizuku 权限管理器
 */
@Composable
fun ShizukuPermissionManager(
    viewModel: ShizukuPermissionViewModel = viewModel(),
    onPermissionGranted: () -> Unit = {},
    content: @Composable (ShizukuPermissionState, () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val permissionState by viewModel.permissionState.collectAsState()

    // 初始化和清理
    DisposableEffect(viewModel) {
        viewModel.initialize()
        onDispose {
            viewModel.cleanup()
        }
    }

    // 监听权限授予状态
    LaunchedEffect(permissionState) {
        if (permissionState == ShizukuPermissionState.GRANTED) {
            onPermissionGranted()
        }
    }

    content(permissionState) {
        viewModel.requestShizukuPermission(context)
    }
}
