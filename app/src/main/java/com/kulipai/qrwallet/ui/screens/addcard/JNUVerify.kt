package com.kulipai.qrwallet.ui.screens.addcard

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.unit.dp
import com.kulipai.qrwallet.ui.theme.AnimatedNavigation
import com.kulipai.qrwallet.util.Prefs
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
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

}

/**
 * 封装了身份验证和获取二维码的逻辑。
 */
class AuthClient {
    companion object {
        private const val KEY_USER_SESSION = "user_session_cookie"
        private const val KEY_SESSION_TIMESTAMP = "session_timestamp"
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
        .add("X-Requested-With", "com.wisedu.cpdaily.jiangnan")
        .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        .add("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8")
        .build()

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
     * 设置基础 Cookie（从原代码提取，保持不变）
     */
    private fun setupBaseCookies(
        AUTHTGC: String,
//        iPlanetDirectoryPro: String,
        route: String
    ) {

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
    suspend fun getAuthInfo(
        AUTHTGC: String,
//        iPlanetDirectoryPro: String,
        route: String
    ) {
        return withContext(Dispatchers.IO) {
            val baseUrl = "http://fanxiaotong.jiangnan.edu.cn"

            // 设置基础 Cookie（保持原逻辑不变）
            setupBaseCookies(
                AUTHTGC,
//                iPlanetDirectoryPro,
                route
            )

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

            userSessionCookie?.let {
                saveSession(userSessionCookie)

            }
        }
    }

    suspend fun getInfos(): String? {
        return withContext(Dispatchers.IO) {
            val qrCodeUrl = "http://fanxiaotong.jiangnan.edu.cn/home/index"
            val qrRequest = Request.Builder().url(qrCodeUrl).headers(headers).build()

            try {
                sessionClient.newCall(qrRequest).execute().use { qrResponse ->
                    if (!qrResponse.isSuccessful) {
                        println("refresh failed with code ${qrResponse.code}")
                        return@withContext null
                    }
                    val responseBody = qrResponse.body.string()
                    return@withContext responseBody
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
fun JNUVerifyScreen(
    navigator: DestinationsNavigator,
) {

    val param_route = JNUCardCache.param_route
    val param_AUTHTGC = JNUCardCache.param_AUTHTGC
//    val param_iPlanetDirectoryPro = JNUCardCache.param_iPlanetDirectoryPro

    val authClient = remember(Unit) { AuthClient() }

    var studentId by remember { mutableStateOf("") }

    var showText by remember { mutableStateOf("请稍等...") }

    LaunchedEffect(Unit) {
        authClient.getAuthInfo(
            param_AUTHTGC,
//            param_iPlanetDirectoryPro,
            param_route
        )
        val infos = authClient.getInfos()
        if (infos != null) {
            val regex = Regex(
                """<div class="attr">学工号</div>.*?"val">(.*?)<""",
                setOf(RegexOption.DOT_MATCHES_ALL)
            )
            val matchResult = regex.find(infos)
            if (matchResult != null) {
                showText = "请确认信息无误"
                studentId = matchResult.groups[1]?.value.toString()
            } else {
                showText = "获取信息失败"
            }
        } else {
            showText = "获取信息失败"
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(showText)
            Spacer(Modifier.height(24.dp))
            Text(
                studentId,
                style = MaterialTheme.typography.headlineMediumEmphasized
            )
            Spacer(Modifier.height(24.dp))
            Button({
                Prefs.putString("jnu_param_id", studentId)

                Prefs.putString("jnu_param_route", param_route)
//                Prefs.putString(
//                    "jnu_param_iPlanetDirectoryPro",
//                    param_iPlanetDirectoryPro
//                )
                Prefs.putString("jnu_param_AUTHTGC", param_AUTHTGC)
                navigator.navigate(HomeScreenDestination) {
                    popUpTo(NavGraphs.root) { inclusive = true }
                }
            }) {
                Text("确认")
            }
        }

    }

}