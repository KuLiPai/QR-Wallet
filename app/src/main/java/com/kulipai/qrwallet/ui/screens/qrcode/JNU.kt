package com.kulipai.qrwallet.ui.screens.qrcode

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.kulipai.qrwallet.ui.theme.AnimatedNavigation
import com.kulipai.qrwallet.util.Prefs
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

/**
 * 一个更健壮的内存 CookieJar 实现，可以正确处理跨域的 Cookie，
 * 类似于 Web 浏览器或 Python 的 requests.Session。
 */
class SessionCookieJar : CookieJar {
    private val cookieStore = ConcurrentHashMap<String, Cookie>()

    private fun cookieKey(cookie: Cookie) = "${cookie.name};${cookie.domain};${cookie.path}"

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.forEach { cookie ->
            cookieStore[cookieKey(cookie)] = cookie
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val validCookies = mutableListOf<Cookie>()
        val cookiesToRemove = mutableListOf<String>()
        val currentTime = System.currentTimeMillis()

        cookieStore.values.forEach { cookie ->
            if (cookie.expiresAt < currentTime) {
                cookiesToRemove.add(cookieKey(cookie))
            } else if (cookie.matches(url)) {
                validCookies.add(cookie)
            }
        }

        cookiesToRemove.forEach { key ->
            cookieStore.remove(key)
        }

        return validCookies
    }

    /**
     * 手动设置 Cookie，用于恢复存储的 user_session
     */
    fun setUserSessionCookie(userSession: String) {
        val httpUrl = "http://fanxiaotong.jiangnan.edu.cn".toHttpUrl()
        val cookie = Cookie.Builder()
            .domain(httpUrl.host)
            .name("user_session")
            .value(userSession)
            .build()
        cookieStore[cookieKey(cookie)] = cookie
    }
}

/**
 * 封装了身份验证和获取二维码的逻辑。
 */
class AuthClient {
    companion object {
        private const val KEY_USER_SESSION = "user_session_cookie"
        private const val KEY_SESSION_TIMESTAMP = "session_timestamp"
        private const val SESSION_EXPIRY_HOURS = 6L // 6小时过期
    }

    private val cookieJar = SessionCookieJar()
    private val sessionClient: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build()
    private val noRedirectClient: OkHttpClient = sessionClient.newBuilder()
        .followRedirects(false)
        .build()

    // 将 headers 定义为类的属性，以便复用
    private val headers = Headers.Builder()
        .add(
            "User-Agent",
            "Mozilla/5.0 (Linux; Android 14; Mi 10S Build/AP1A.240505.005; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/139.0.7258.94 Mobile Safari/537.36 cpdaily/9.6.4 wisedu/9.6.4"
        )
        .add("X-Requested-With", "com.wisedu.cpdaily.jiangnan")
        .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        .add("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8")
        .build()

    /**
     * 检查本地存储的 userSession 是否仍然有效
     */
    private fun getValidStoredSession(): String? {
        val storedSession = Prefs.getString(KEY_USER_SESSION)
        val sessionTimestamp = Prefs.getString(KEY_SESSION_TIMESTAMP).toLongOrNull()

        if (storedSession.isEmpty() || sessionTimestamp == null) {
            return null
        }

        val currentTime = System.currentTimeMillis()
        val sessionAge = (currentTime - sessionTimestamp) / (1000 * 60 * 60) // 转换为小时

        return if (sessionAge < SESSION_EXPIRY_HOURS) {
            Log.d("AuthClient", "使用存储的有效 Session，已使用 $sessionAge 小时")
            storedSession
        } else {
            Log.d("AuthClient", "存储的 Session 已过期（已使用 $sessionAge 小时），需要重新获取")
            null
        }
    }

    /**
     * 保存 userSession 到本地存储
     */
    private fun saveSession(userSession: String) {
        val currentTime = System.currentTimeMillis()
        Prefs.putString(KEY_USER_SESSION, userSession)
        Prefs.putString(KEY_SESSION_TIMESTAMP, currentTime.toString())
        Log.d("AuthClient", "Session 已保存到本地存储")
    }

    /**
     * 智能获取二维码：优先使用存储的Session刷新，失败时重新认证
     */
    suspend fun getQrCodeSmart(): Pair<String?, String?> {
        return withContext(Dispatchers.IO) {
            // 1. 尝试使用存储的 Session
            val storedSession = getValidStoredSession()

            if (storedSession != null) {
                // 设置基础 Cookie
                setupBaseCookies()
                // 设置存储的 user_session Cookie
                cookieJar.setUserSessionCookie(storedSession)

                Log.d("AuthClient", "尝试使用存储的 Session 刷新二维码")
                val qrCode = refreshQrCode()
                if (qrCode != null) {
                    Log.d("AuthClient", "使用存储的 Session 成功获取二维码")
                    return@withContext Pair(storedSession, qrCode)
                } else {
                    Log.d("AuthClient", "存储的 Session 刷新二维码失败，需要重新获取 Session")
                }
            }

            // 2. 如果没有有效 Session 或刷新失败，执行完整认证
            Log.d("AuthClient", "执行完整的身份验证流程")
            val (newSession, qrCode) = getAuthInfo()

            if (newSession != null && qrCode != null) {
                // 保存新获取的 Session
                saveSession(newSession)
                Log.d("AuthClient", "成功获取新的 Session 和二维码")
                return@withContext Pair(newSession, qrCode)
            } else {
                Log.e("AuthClient", "获取新的 Session 和二维码失败")
                return@withContext Pair(null, null)
            }
        }
    }

    /**
     * 设置基础 Cookie（从原代码提取，保持不变）
     */
    private fun setupBaseCookies() {
        val AUTHTGC = Prefs.getString("jnu_param_AUTHTGC")
//        val iPlanetDirectoryPro = Prefs.getString("jnu_param_iPlanetDirectoryPro")
        val route = Prefs.getString("jnu_param_route")


        val baseCookies = mapOf(
            "AUTHTGC" to AUTHTGC,
            "CASTGC" to AUTHTGC,
//            "iPlanetDirectoryPro" to iPlanetDirectoryPro,
            "route" to route
        )

        val httpUrl = "http://jiangnan.edu.cn".toHttpUrl()
        val cookieList = baseCookies.map { (name, value) ->
            Cookie.Builder().domain(httpUrl.host).name(name).value(value).build()
        }
        cookieJar.saveFromResponse(httpUrl, cookieList)
    }

    /**
     * 执行初始身份验证流程并获取 user_session 和二维码数据。
     * @return 返回一个 Pair，其中 first 是 user_session，second 是 qrcode 字符串。
     */
    suspend fun getAuthInfo(): Pair<String?, String?> {
        return withContext(Dispatchers.IO) {
            val baseUrl = "http://fanxiaotong.jiangnan.edu.cn"

            // 设置基础 Cookie（保持原逻辑不变）
            setupBaseCookies()

//            println("Step 1: 请求 /passport/auth")
            val step1Url = "$baseUrl/passport/auth"
            val request1 = Request.Builder().url(step1Url).headers(headers).build()
            val response1 = noRedirectClient.newCall(request1).execute()

            if (!response1.isRedirect) {
                throw Exception("Step 1 failed: Expected a redirect, but got status code ${response1.code}")
            }

            val location = response1.header("Location")
//            println("Status: ${response1.code}")
//            println("Location: $location")

            val userSessionCookie = response1.headers("Set-Cookie")
                .firstOrNull { it.startsWith("user_session=") }
                ?.split(";")?.get(0)?.split("=")?.get(1)
//            println("Set-Cookie: user_session=${userSessionCookie}")

            if (location == null) {
                throw Exception("Step 1 failed: Location header not found.")
            }

//            println("\nStep 2 & 3: 跟随 Location 进入 authserver 并完成回调")
            val request2 = Request.Builder().url(location).headers(headers).build()
            sessionClient.newCall(request2).execute().use { finalAuthResponse ->
                println("最终跳转 URL: ${finalAuthResponse.request.url}")
                if (!finalAuthResponse.isSuccessful) {
                    throw Exception("Step 2/3 failed: Final auth request failed with code ${finalAuthResponse.code}")
                }
            }

            val qrCode = refreshQrCode()

            Pair(userSessionCookie, qrCode)
        }
    }

    /**
     * 刷新二维码。此函数假定 getAuthInfo() 已经成功调用，
     * 并且 sessionClient 的 cookieJar 中已包含有效的会话 cookie。
     * @return 返回新的 qrcode 字符串，如果失败则返回 null。
     */
    suspend fun refreshQrCode(): String? {
        return withContext(Dispatchers.IO) {
//            println("Refreshing QR Code...")
            val qrCodeUrl = "http://fanxiaotong.jiangnan.edu.cn/home/get_qrcode"
            val qrRequest = Request.Builder().url(qrCodeUrl).headers(headers).build()

            try {
                sessionClient.newCall(qrRequest).execute().use { qrResponse ->
                    if (!qrResponse.isSuccessful) {
                        println("QR Code refresh failed with code ${qrResponse.code}")
                        return@withContext null
                    }
                    val responseBody = qrResponse.body.string()
                    if (!responseBody.trim().startsWith("{")) {
                        println("QR Code refresh failed: Expected JSON but received: $responseBody")
                        return@withContext null
                    }
                    val newQrCode = JSONObject(responseBody).getString("qrcode")
                    println("New QR Code received.")
                    return@withContext newQrCode
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun JNUScreen(
    // navigator: DestinationsNavigator, // 如果需要，请取消注释
) {
    val qrColor = MaterialTheme.colorScheme.onBackground
    // 使用 remember(Unit) 确保 AuthClient 实例在重组之间保持不变
    val authClient = remember(Unit) { AuthClient() }

    var qrCode by remember { mutableStateOf<String?>(null) }
    var userSession by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Effect 1: 仅在 Composable 首次进入时执行一次，使用智能获取方法
    LaunchedEffect(Unit) {
        try {
            val (session, initialQrCode) = authClient.getQrCodeSmart()
            if (session != null && initialQrCode != null) {
                userSession = session
                qrCode = initialQrCode
                Log.d("JNUScreen", "成功获取! User Session: $userSession, QR Code: $qrCode")
            } else {
                errorMessage = "未能获取到 Session 或二维码"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "获取失败: ${e.message}"
            Log.d("JNUScreen", "获取失败: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // Effect 2: 当 userSession 成功获取后开始执行，并持续运行以刷新二维码
    LaunchedEffect(userSession) {
        // 确保只有在成功登录后才启动刷新循环
        if (userSession != null) {
            while (true) {
                delay(7000) // 等待7秒
                try {
                    val newQrCode = authClient.refreshQrCode()
                    if (newQrCode != null) {
                        qrCode = newQrCode
                    }
                } catch (e: Exception) {
                    // 记录刷新错误，但不要中断循环
                    e.printStackTrace()
                    Log.e("JNUScreen", "二维码刷新失败: ${e.message}")
                }
            }
        }
    }

    val qrcodePainter: Painter = rememberQrCodePainter(qrCode ?: "loading...") {
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
        when {
            errorMessage != null -> {
                Text(
                    text = "错误: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            else -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "QR Code",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Spacer(Modifier.height(48.dp))
                    if (isLoading) {
                        LoadingIndicator(
                            modifier = Modifier.size(48.dp),
                        )
                    } else {
                        Image(
                            painter = qrcodePainter,
                            contentDescription = "QR Code",
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }
            }
        }
    }
}