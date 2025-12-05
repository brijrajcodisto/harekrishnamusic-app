package com.musicstreaming.data.api

import com.musicstreaming.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import com.musicstreaming.data.auth.AuthManager

/**
 * Network interceptor to add authentication headers
 */
class AuthInterceptor(private val authManager: AuthManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth for public endpoints
        if (originalRequest.url.encodedPath.contains("public")) {
            return chain.proceed(originalRequest)
        }

        // Add auth token to header
        val token = authManager.getToken()
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .header("X-API-Key", BuildConfig.MUSIC_API_KEY)
                .header("X-API-Secret", BuildConfig.MUSIC_API_SECRET)
                .build()
        } else {
            originalRequest.newBuilder()
                .header("X-API-Key", BuildConfig.MUSIC_API_KEY)
                .header("X-API-Secret", BuildConfig.MUSIC_API_SECRET)
                .build()
        }

        return chain.proceed(newRequest)
    }
}

/**
 * Network interceptor for logging
 */
class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()

        val response = chain.proceed(request)

        val duration = System.currentTimeMillis() - startTime
        println("${request.method} ${request.url} took ${duration}ms - ${response.code}")

        return response
    }
}
