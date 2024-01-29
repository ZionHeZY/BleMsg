package com.hezy.domain

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.hezy.model.gateway.HandleClientGateway
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HandleClientGatewayImpl : HandleClientGateway {
    @SuppressLint("MissingPermission")
    override suspend fun initClient(
        bluetoothDevice: BluetoothDevice,
        bluetoothAdapter: BluetoothAdapter,
        isSecure: Boolean
    ): BluetoothData.Data<BluetoothSocket> {
        return try {
            val clientResult = withContext(Dispatchers.IO) {
                bluetoothAdapter.cancelDiscovery()
                if (isSecure) {
                    bluetoothDevice.createRfcommSocketToServiceRecord(
                        BluetoothConstants.UUID_SECURE
                    )
                } else {
                    bluetoothDevice.createInsecureRfcommSocketToServiceRecord(
                        BluetoothConstants.UUID_INSECURE
                    )
                }
            }
            BluetoothData.Data(ConnectionState.Inited, clientResult)
        } catch (e: Exception) {
            BluetoothData.Data(ConnectionState.Failed)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun connectTo(bluetoothSocket: BluetoothSocket): BluetoothData<ConnectionState> {
        return try {
            withContext(Dispatchers.IO) {
                bluetoothSocket.connect()
            }
            BluetoothData.Data(ConnectionState.Connected)
        } catch (e: Exception) {
            BluetoothData.Data(ConnectionState.Failed)
        }
    }

    override suspend fun closeClient(bluetoothSocket: BluetoothSocket): BluetoothData<ConnectionState> {
        return try {
            withContext(Dispatchers.IO) {
                bluetoothSocket.close()
            }
            BluetoothData.Data(ConnectionState.Closed)
        } catch (e: Exception) {
            BluetoothData.Data(ConnectionState.Failed)
        }
    }
}