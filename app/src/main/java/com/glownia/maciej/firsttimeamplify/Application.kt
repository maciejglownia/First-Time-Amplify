package com.glownia.maciej.firsttimeamplify

import android.app.Application

class FirstTimeAmplifyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // initialize Amplify when application is starting
        Backend.initialize(applicationContext)
    }
}