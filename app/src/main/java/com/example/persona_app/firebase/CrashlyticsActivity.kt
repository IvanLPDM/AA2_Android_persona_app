package com.example.persona_app.firebase

import com.google.firebase.crashlytics.KeyValueBuilder
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class CrashlyticsActivity {
    private val crashlytics = Firebase.crashlytics

    fun logSimpleError(text: String, addExtraDataLambda: (KeyValueBuilder.() -> Unit)? = null)
    {
        logError(Exception(text), addExtraDataLambda)
    }

    fun logError(exception: Exception, addExtraDataLambda: (KeyValueBuilder.() -> Unit)? = null)
    {
        addExtraDataLambda?.let { lambda ->
            val builder = KeyValueBuilder(crashlytics)
            builder.lambda()
        }

        crashlytics.recordException(exception)
    }
}