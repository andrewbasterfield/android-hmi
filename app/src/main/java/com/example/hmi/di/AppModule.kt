package com.example.hmi.di

import com.example.hmi.protocol.PlcCommunicator
import com.example.hmi.protocol.RawTcpPlcCommunicator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindPlcCommunicator(
        rawTcpPlcCommunicator: RawTcpPlcCommunicator
    ): PlcCommunicator
}
