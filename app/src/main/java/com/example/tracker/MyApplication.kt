package com.example.tracker

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseApp.getInstance
import com.google.firebase.FirebaseApp.initializeApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference

@HiltAndroidApp
class MyApplication:Application() {
    companion object {

        private lateinit var appContext: WeakReference<Context>
       // private var firebaseApp = getAppContext()?.let { initializeApp(it) }

        fun getAppContext(): Context? {
            return appContext.get()
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = WeakReference(applicationContext)
       // firebaseApp = getInstance()
       //FirebaseApp.initializeApp(this)
    }
}