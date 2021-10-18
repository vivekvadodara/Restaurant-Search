

package com.demo.di.module

import com.demo.data.repository.RestaurantRepository
import com.demo.data.repository.RestaurantRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@InstallIn(ActivityRetainedComponent::class)
@Module
abstract class DemoRepositoryModule {

    @ActivityRetainedScoped
    @Binds
    abstract fun bindRestaurantRepository(repository: RestaurantRepositoryImpl): RestaurantRepository

}