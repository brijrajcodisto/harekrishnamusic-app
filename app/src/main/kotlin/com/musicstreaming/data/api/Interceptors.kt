package com.musicstreaming.data.api

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
                .build()
        } else {
            originalRequest
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
