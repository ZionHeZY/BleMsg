package com.hezy.blemsg.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.hezy.domain.factory.BluetoothFactory
import com.hezy.model.gateway.HandleClientGateway
import com.hezy.model.gateway.HandleMessageGateway
import com.hezy.model.usecase.client.HandleClientUseCase
import com.hezy.model.usecase.message.ReadMessageUseCase
import com.hezy.model.usecase.message.WriteMessageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

/**
 * 自定义工厂类，用于创建 ReadMessageUseCase
 */
class ReadMessageUseCaseFactory @Inject constructor(
    private val handleMessageGateway: HandleMessageGateway,
    private val bluetoothFactory: BluetoothFactory
) {
    fun create(): ReadMessageUseCase? {
        val socket = bluetoothFactory.getCurrentSocket() ?: return null

        return ReadMessageUseCase(
            bluetoothSocket = socket,
            handleMessageGateway = handleMessageGateway
        )
    }
}

/**
 * 自定义工厂类，用于创建 WriteMessageUseCase
 */
class WriteMessageUseCaseFactory @Inject constructor(
    private val handleMessageGateway: HandleMessageGateway,
    private val bluetoothFactory: BluetoothFactory
) {
    fun create(): WriteMessageUseCase? {
        val socket = bluetoothFactory.getCurrentSocket() ?: return null

        return WriteMessageUseCase(
            bluetoothSocket = socket,
            handleMessageGateway = handleMessageGateway
        )
    }
}

/**
 * ViewModel模块依赖注入
 */
@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    /**
     * 提供客户端连接用例工厂
     */
    @Provides
    @ViewModelScoped
    fun provideHandleClientUseCaseFactory(
        handleClientGateway: HandleClientGateway,
        bluetoothAdapter: BluetoothAdapter,
        bluetoothFactory: BluetoothFactory
    ): (@JvmSuppressWildcards BluetoothDevice, @JvmSuppressWildcards Boolean) -> @JvmSuppressWildcards HandleClientUseCase {
        return { device, isSecure ->
            // 设置当前设备
            bluetoothFactory.setCurrentDevice(device)

            // 创建用例
            HandleClientUseCase(
                handleClientGateway = handleClientGateway,
                bluetoothDevice = device,
                bluetoothAdapter = bluetoothAdapter,
                isSecure = isSecure
            )
        }
    }
}
