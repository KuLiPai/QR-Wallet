package com.kulipai.qrwallet
import android.app.Application
import com.kulipai.qrwallet.util.Prefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Prefs.init(this)  // 初始化 SharedPreferences 单例
    }
}