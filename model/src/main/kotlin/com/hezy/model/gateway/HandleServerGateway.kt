package com.hezy.model.gateway

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ConnectionState

interface HandleServerGateway {
    suspend fun initServer(
        bluetoothAdapter: BluetoothAdapter,
        secure: Boolean
    ): BluetoothData.Data<BluetoothServerSocket>

    suspend fun connectFrom(bluetoothServerSocket: BluetoothServerSocket): BluetoothData<ConnectionState>
    suspend fun closeServer(bluetoothServerSocket: BluetoothServerSocket): BluetoothData<ConnectionState>
}