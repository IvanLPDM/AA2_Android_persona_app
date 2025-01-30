package com.example.persona_app.firebase

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class   CrashlyticsActivity {
    private val crashlytics = Firebase.crashlytics

    fun logSimpleError(text: String, addExtraDataLambda: (() -> Map<String, String>)? = null) {
        logError(Exception(text), addExtraDataLambda)
    }

    fun logError(exception: Exception, addExtraDataLambda: (() -> Map<String, String>)? = null) {
        // Agrega claves y valores personalizados
        addExtraDataLambda?.invoke()?.forEach { (key, value) ->
            crashlytics.setCustomKey(key, value)
        }

        // Registra la excepción
        crashlytics.recordException(exception)
    }
}