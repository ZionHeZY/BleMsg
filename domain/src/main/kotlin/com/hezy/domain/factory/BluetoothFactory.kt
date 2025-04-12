package com.hezy.domain.factory

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 蓝牙工厂类，用于动态提供蓝牙设备和Socket
 */
@Singleton
class BluetoothFactory @Inject constructor() {
    
    // 当前选中的蓝牙设备
    private var currentDevice: BluetoothDevice? = null
    
    // 当前的蓝牙Socket
    private var currentSocket: BluetoothSocket? = null
    
    /**
     * 设置当前蓝牙设备
     */
    fun setCurrentDevice(device: BluetoothDevice) {
        currentDevice = device
    }
    
    /**
     * 获取当前蓝牙设备
     */
    fun getCurrentDevice(): BluetoothDevice? {
        return currentDevice
    }
    
    /**
     * 设置当前蓝牙Socket
     */
    fun setCurrentSocket(socket: BluetoothSocket) {
        currentSocket = socket
    }
    
    /**
     * 获取当前蓝牙Socket
     */
    fun getCurrentSocket(): BluetoothSocket? {
        return currentSocket
    }
    
    /**
     * 清除当前Socket
     */
    fun clearCurrentSocket() {
        currentSocket = null
    }
    
    /**
     * 清除当前设备
     */
    fun clearCurrentDevice() {
        currentDevice = null
    }
}
