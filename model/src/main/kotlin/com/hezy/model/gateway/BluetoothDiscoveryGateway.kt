package com.hezy.model.gateway

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.hezy.model.entity.Devices
import kotlinx.coroutines.flow.Flow

/**
 * 蓝牙设备发现网关接口
 */
interface BluetoothDiscoveryGateway {
    /**
     * 开始扫描蓝牙设备
     * @param bluetoothAdapter 蓝牙适配器
     * @return 设备流
     */
    fun startDiscovery(bluetoothAdapter: BluetoothAdapter): Flow<Devices>
    
    /**
     * 停止扫描
     * @param bluetoothAdapter 蓝牙适配器
     */
    suspend fun stopDiscovery(bluetoothAdapter: BluetoothAdapter)
    
    /**
     * 获取已配对设备
     * @param bluetoothAdapter 蓝牙适配器
     * @return 已配对设备列表
     */
    suspend fun getPairedDevices(bluetoothAdapter: BluetoothAdapter): List<Devices>
    
    /**
     * 将BluetoothDevice转换为Devices
     * @param device 蓝牙设备
     * @return Devices对象
     */
    fun mapToDevices(device: BluetoothDevice): Devices
}
