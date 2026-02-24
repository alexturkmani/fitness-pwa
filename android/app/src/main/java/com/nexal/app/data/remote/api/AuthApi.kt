package com.nexal.app.data.remote.api

import com.nexal.app.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @POST("api/auth/mobile/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<RegisterResponseDto>

    @POST("api/auth/mobile/google")
    suspend fun googleSignIn(@Body request: GoogleSignInRequestDto): Response<LoginResponseDto>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): Response<SuccessResponseDto>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<SuccessResponseDto>

    @POST("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): Response<SuccessResponseDto>

    @POST("api/auth/change-email")
    suspend fun changeEmail(@Body request: ChangeEmailRequestDto): Response<SuccessResponseDto>

    @POST("api/auth/subscription")
    suspend fun updateSubscription(@Body request: SubscriptionActionDto): Response<SuccessResponseDto>

    @POST("api/auth/mobile/start-trial")
    suspend fun startTrial(): Response<StartTrialResponseDto>

    /** Refresh user info (subscription status, etc.) */
    @GET("api/auth/mobile/me")
    suspend fun getMe(): Response<UserDto>
}
