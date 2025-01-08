package com.example.persona_app.firebase

import android.app.Application

class FirebaseActivity {
    companion object {

        lateinit var analytics: AnalyticsActivity
        val crashlytics = CrashlyticsActivity()
        val storage = StorageActivity()

        fun init(appContext: Application) {
            analytics = AnalyticsActivity(appContext)
        }
    }
}