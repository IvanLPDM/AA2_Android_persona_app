package com.example.persona_app.firebase

import android.app.Application

class FirebaseActivity {
    companion object {

        lateinit var analytics: com.example.persona_app.firebase.AnalyticsActivity
        val crashlytics = com.example.persona_app.firebase.CrashlyticsActivity()

        fun init(appContext: Application) {
            analytics = com.example.persona_app.firebase.AnalyticsActivity(appContext)
        }
    }
}