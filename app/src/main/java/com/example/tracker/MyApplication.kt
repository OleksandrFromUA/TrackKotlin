package com.example.tracker

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltAndroidApp
 class MyApplication:Application(), Configuration.Provider {

     @Inject lateinit var hiltWorkerFactory: HiltWorkerFactory
   //@Inject lateinit var hiltWorkerFactory: MyWorkerFactory
    companion object {

        private lateinit var appContext: WeakReference<Context>

       /* fun getAppContext(): Context? {
            return appContext.get()
        }*/

    }

    override fun onCreate() {
        super.onCreate()
        appContext = WeakReference(applicationContext)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()


    /*class MyWorkerFactory @Inject constructor(private val myRoomDB: MyRoomDB):WorkerFactory(){
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker? = MyWorker(appContext,workerParameters, myRoomDB)

    }*/
}
