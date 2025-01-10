package com.example.lupus_v2

import android.app.Application
import com.example.lupus_v2.data.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LupusApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@LupusApplication)
            androidLogger()

            modules(appModule)
        }
    }

}