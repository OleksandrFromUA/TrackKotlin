package com.example.database

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference
//@HiltAndroidApp
class MyAppApplication: Application() {
    companion object {

        private lateinit var appContext: WeakReference<Context>

        fun getAppContext(): Context? {
            return appContext.get()
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = WeakReference(applicationContext)
       // FirebaseApp.initializeApp(this)
    }
}
