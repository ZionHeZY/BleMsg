package com.hezy.model.gateway

import android.bluetooth.BluetoothSocket
import com.hezy.model.entity.Messages
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ConnectionState
import java.io.InputStream
import java.io.OutputStream

interface HandleMessageGateway {
    fun initTransferMessage(bluetoothSocket: BluetoothSocket): BluetoothData.Data<BluetoothSocket>
    suspend fun readMessage(inputStream: InputStream): BluetoothData.Data<Messages>
    suspend fun writeMessage(
        buffer: ByteArray,
        outputStream: OutputStream
    ): BluetoothData.Data<Messages>
    suspend fun closeTransfer(bluetoothSocket: BluetoothSocket): BluetoothData<ConnectionState>
}