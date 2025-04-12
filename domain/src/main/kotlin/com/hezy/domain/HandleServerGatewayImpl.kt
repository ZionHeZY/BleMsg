package com.hezy.domain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import com.hezy.model.gateway.HandleServerGateway
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import javax.inject.Inject

class HandleServerGatewayImpl @Inject constructor() : HandleServerGateway {
    @SuppressLint("MissingPermission")
    override suspend fun initServer(
        bluetoothAdapter: BluetoothAdapter,
        secure: Boolean
    ): BluetoothData.Data<BluetoothServerSocket> {
        return try {
            val clientResult = withContext(Dispatchers.IO) {
                bluetoothAdapter.cancelDiscovery()
                if (secure) bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                    BluetoothConstants.NAME_SECURE,
                    BluetoothConstants.UUID_SECURE
                ) else bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                    BluetoothConstants.NAME_INSECURE,
                    BluetoothConstants.UUID_INSECURE
                )
            }
            BluetoothData.Data(ConnectionState.Inited, clientResult)
        } catch (e: Exception) {
            BluetoothData.Data(ConnectionState.Failed)
        }
    }

    override suspend fun connectFrom(bluetoothServerSocket: BluetoothServerSocket): BluetoothData<ConnectionState> {
        return try {
            withContext(Dispatchers.IO) {
                bluetoothServerSocket.accept()
            }
            BluetoothData.Data(ConnectionState.Connected)
        } catch (e: Exception) {
            BluetoothData.Data(ConnectionState.Failed)
        }
    }

    override suspend fun closeServer(bluetoothServerSocket: BluetoothServerSocket): BluetoothData<ConnectionState> {
        return try {
            withContext(Dispatchers.IO) {
                bluetoothServerSocket.close()
            }
            BluetoothData.Data(ConnectionState.Closed)
        } catch (e: Exception) {
            BluetoothData.Data(ConnectionState.Failed)
        }
    }
}