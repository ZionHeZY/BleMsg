package com.hezy.domain.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

/**
 * 蓝牙模块依赖注入
 */
@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    /**
     * 提供蓝牙适配器
     */
    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter
    }

    /**
     * 提供目标蓝牙设备
     */
    @Provides
    @Named("targetDevice")
    fun provideTargetDevice(factory: com.hezy.domain.factory.BluetoothFactory): BluetoothDevice {
        return factory.getCurrentDevice()
            ?: throw IllegalStateException("没有选中的蓝牙设备")
    }

    /**
     * 提供蓝牙Socket
     */
    @Provides
    @Named("bluetoothSocket")
    fun provideBluetoothSocket(factory: com.hezy.domain.factory.BluetoothFactory): BluetoothSocket {
        return factory.getCurrentSocket()
            ?: throw IllegalStateException("没有可用的蓝牙连接")
    }
}
