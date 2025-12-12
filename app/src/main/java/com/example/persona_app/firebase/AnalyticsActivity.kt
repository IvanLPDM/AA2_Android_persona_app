package com.example.persona_app.firebase

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsActivity(val appContext: Application) {
    private val analytics = FirebaseAnalytics.getInstance(appContext)

    fun logOpenApp()
    {
        val bundle = Bundle()
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)
    }

    fun logLogin(method: String)
    {
        val bundle = Bundle()

        bundle.putString(FirebaseAnalytics.Param.METHOD, method)
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }
}