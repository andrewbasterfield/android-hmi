package com.example.hmi

import android.app.Application
import com.example.hmi.protocol.DemoPlcServer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HmiApplication : Application() {
    @Inject
    lateinit var demoServer: DemoPlcServer

    override fun onCreate() {
        super.onCreate()
        // Start the demo server on the default port 9999
        demoServer.start()
    }
}
