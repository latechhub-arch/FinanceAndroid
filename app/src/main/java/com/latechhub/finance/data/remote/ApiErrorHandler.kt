package com.latechhub.finance.data.remote

import org.json.JSONObject
import retrofit2.HttpException

object ApiErrorHandler {

    fun getMessage(exception: Throwable, fallback: String): String {
        if (exception is HttpException) {
            val errorBody = exception.response()?.errorBody()?.string()

            if (!errorBody.isNullOrBlank()) {
                try {
                    val json = JSONObject(errorBody)
                    val message = json.optString("message").trim()

                    if (message.isNotEmpty()) {
                        return message
                    }
                } catch (_: Exception) {
                    // Fall through to the Retrofit exception message.
                }
            }

            return exception.message()
        }

        return exception.message ?: fallback
    }
}
