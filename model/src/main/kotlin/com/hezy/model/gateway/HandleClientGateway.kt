package com.hezy.model.gateway

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ConnectionState

interface HandleClientGateway {
    suspend fun initClient(
        bluetoothDevice: BluetoothDevice,
        bluetoothAdapter: BluetoothAdapter,
        isSecure: Boolean
    ): BluetoothData.Data<BluetoothSocket>

    suspend fun connectTo(bluetoothSocket: BluetoothSocket): BluetoothData<ConnectionState>
    suspend fun closeClient(bluetoothSocket: BluetoothSocket): BluetoothData<ConnectionState>
}