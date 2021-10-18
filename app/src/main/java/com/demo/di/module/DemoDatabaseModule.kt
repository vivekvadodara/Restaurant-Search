package com.demo.di.module

import android.app.Application
import com.demo.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DemoDatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(application: Application) = AppDatabase.getInstance(application)

    @Singleton
    @Provides
    fun provideRestaurantDao(database: AppDatabase) = database.getRestaurantDao()

    @Singleton
    @Provides
    fun provideMenuDao(database: AppDatabase) = database.getMenuDao()
}
