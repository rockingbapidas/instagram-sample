package com.example.instagramclone.di

import com.example.instagramclone.data.local.AppDatabase
import com.example.instagramclone.data.local.preferences.AuthPreferences
import com.example.instagramclone.data.remote.api.UserApi
import com.example.instagramclone.data.repository.AuthRepositoryImpl
import com.example.instagramclone.domain.repository.AuthRepository
import com.example.instagramclone.domain.usecase.auth.GetCurrentUserUseCase
import com.example.instagramclone.domain.usecase.auth.IsAuthenticatedUseCase
import com.example.instagramclone.domain.usecase.auth.LoginUseCase
import com.example.instagramclone.domain.usecase.auth.LogoutUseCase
import com.example.instagramclone.domain.usecase.auth.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthPreferences(
        @ApplicationContext context: android.content.Context
    ): AuthPreferences = AuthPreferences(context)

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: UserApi,
        db: AppDatabase,
        authPreferences: AuthPreferences
    ): AuthRepository = AuthRepositoryImpl(
        api = api,
        userDao = db.userDao(),
        authPreferences = authPreferences
    )

    @Provides
    fun provideLoginUseCase(authRepository: AuthRepository) = LoginUseCase(authRepository)

    @Provides
    fun provideRegisterUseCase(authRepository: AuthRepository) = RegisterUseCase(authRepository)

    @Provides
    fun provideLogoutUseCase(authRepository: AuthRepository) = LogoutUseCase(authRepository)

    @Provides
    fun provideGetCurrentUserUseCase(authRepository: AuthRepository) = GetCurrentUserUseCase(authRepository)

    @Provides
    fun provideIsAuthenticatedUseCase(authRepository: AuthRepository) = IsAuthenticatedUseCase(authRepository)
}
