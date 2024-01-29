package com.hezy.domain.di

import com.hezy.domain.HandleClientGatewayImpl
import com.hezy.domain.HandleMessageGatewayImpl
import com.hezy.domain.HandleServerGatewayImpl
import com.hezy.model.gateway.HandleClientGateway
import com.hezy.model.gateway.HandleMessageGateway
import com.hezy.model.gateway.HandleServerGateway
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainBindsModule {
    @Singleton
    @Binds
    abstract fun bindHandleClient(impl: HandleClientGatewayImpl): HandleClientGateway

    @Singleton
    @Binds
    abstract fun bindHandleServer(impl: HandleServerGatewayImpl): HandleServerGateway

    @Singleton
    @Binds
    abstract fun bindHandleMessage(impl: HandleMessageGatewayImpl): HandleMessageGateway
}