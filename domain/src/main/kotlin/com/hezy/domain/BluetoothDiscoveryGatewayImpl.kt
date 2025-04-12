package com.hezy.domain

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.RequiresPermission
import com.hezy.model.entity.Devices
import com.hezy.model.gateway.BluetoothDiscoveryGateway
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * 蓝牙设备发现网关实现
 */
class BluetoothDiscoveryGatewayImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BluetoothDiscoveryGateway {

    @SuppressLint("MissingPermission")
    override fun startDiscovery(bluetoothAdapter: BluetoothAdapter): Flow<Devices> = callbackFlow {
        // 创建广播接收器
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        // 发现设备
                        val device = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        }
                        device?.let {
                            trySend(mapToDevices(it))
                        }
                    }
                }
            }
        }

        // 注册广播接收器
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, filter)

        // 开始扫描
        bluetoothAdapter.startDiscovery()

        // 当Flow被取消时，取消注册广播接收器并停止扫描
        awaitClose {
            context.unregisterReceiver(receiver)
            bluetoothAdapter.cancelDiscovery()
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun stopDiscovery(bluetoothAdapter: BluetoothAdapter) {
        withContext(Dispatchers.IO) {
            bluetoothAdapter.cancelDiscovery()
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getPairedDevices(bluetoothAdapter: BluetoothAdapter): List<Devices> {
        return withContext(Dispatchers.IO) {
            bluetoothAdapter.bondedDevices?.map { device ->
                mapToDevices(device)
            } ?: emptyList()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun mapToDevices(device: BluetoothDevice): Devices {
        return Devices(
            deviceName = device.name ?: "未知设备",
            deviceAddress = device.address
        )
    }
}
