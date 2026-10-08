package com.industri.fleettrack

import android.app.Application
import timber.log.Timber

class FleetTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
