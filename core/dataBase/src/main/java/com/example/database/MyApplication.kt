package com.example.database

import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference

class MyApplication: Application() {
    companion object {
        private lateinit var appContext: WeakReference<Context>

        fun getAppContext(): Context? {
            return appContext.get()
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = WeakReference(applicationContext)
    }
}