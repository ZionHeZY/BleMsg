package com.hezy.domain.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.hezy.model.gateway.HandleClientGateway
import com.hezy.model.gateway.HandleMessageGateway
import com.hezy.model.gateway.HandleServerGateway
import com.hezy.model.usecase.client.HandleClientUseCase
import com.hezy.model.usecase.message.ReadMessageUseCase
import com.hezy.model.usecase.message.WriteMessageUseCase
import com.hezy.model.usecase.server.HandleServerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

/**
 * 提供用例的依赖注入模块
 */
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    /**
     * 提供客户端连接用例
     */
    @Provides
    @ViewModelScoped
    fun provideHandleClientUseCase(
        handleClientGateway: HandleClientGateway,
        @Named("targetDevice") bluetoothDevice: BluetoothDevice,
        bluetoothAdapter: BluetoothAdapter,
        @Named("isSecureConnection") isSecure: Boolean
    ): HandleClientUseCase {
        return HandleClientUseCase(
            handleClientGateway = handleClientGateway,
            bluetoothDevice = bluetoothDevice,
            bluetoothAdapter = bluetoothAdapter,
            isSecure = isSecure
        )
    }

    /**
     * 提供服务端连接用例
     */
    @Provides
    @ViewModelScoped
    fun provideHandleServerUseCase(
        handleServerGateway: HandleServerGateway,
        bluetoothAdapter: BluetoothAdapter,
        @Named("isSecureConnection") isSecure: Boolean
    ): HandleServerUseCase {
        return HandleServerUseCase(
            handleServerGateway = handleServerGateway,
            bluetoothAdapter = bluetoothAdapter,
            isSecure = isSecure
        )
    }

    /**
     * 提供读取消息用例
     */
    @Provides
    @ViewModelScoped
    fun provideReadMessageUseCase(
        @Named("bluetoothSocket") bluetoothSocket: BluetoothSocket,
        handleMessageGateway: HandleMessageGateway
    ): ReadMessageUseCase {
        return ReadMessageUseCase(
            bluetoothSocket = bluetoothSocket,
            handleMessageGateway = handleMessageGateway
        )
    }

    /**
     * 提供写入消息用例
     */
    @Provides
    @ViewModelScoped
    fun provideWriteMessageUseCase(
        @Named("bluetoothSocket") bluetoothSocket: BluetoothSocket,
        handleMessageGateway: HandleMessageGateway
    ): WriteMessageUseCase {
        return WriteMessageUseCase(
            bluetoothSocket = bluetoothSocket,
            handleMessageGateway = handleMessageGateway
        )
    }

    /**
     * 提供默认的连接安全设置
     */
    @Provides
    @Named("isSecureConnection")
    fun provideIsSecureConnection(): Boolean {
        return true // 默认使用安全连接
    }
}
