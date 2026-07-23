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
        // Demo/testing convenience only -- an unauthenticated TCP listener that
        // accepts writes has no place in a release build on a plant network.
        if (BuildConfig.DEBUG) {
            demoServer.start()
        }
    }
}
