package com.example.rentmycar.di

import android.content.Context
import com.example.rentmycar.PreferencesManager
import com.example.rentmycar.api.ApiClient
import com.example.rentmycar.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideApiService(preferencesManager: PreferencesManager): ApiService {
        return ApiClient.createApiService(preferencesManager.jwtToken)
    }

    @Singleton
    @Provides
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }
}
