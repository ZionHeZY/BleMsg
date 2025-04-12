package com.hezy.domain

import android.bluetooth.BluetoothSocket
import com.hezy.domain.utils.SocketTools.toCustomString
import com.hezy.model.Constants
import com.hezy.model.entity.Messages
import com.hezy.model.gateway.HandleMessageGateway
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

class HandleMessageGatewayImpl : HandleMessageGateway {
    override fun initTransferMessage(bluetoothSocket: BluetoothSocket): BluetoothData.Data<BluetoothSocket> {
        return BluetoothData.Data(ConnectionState.Inited, bluetoothSocket)
    }

    override suspend fun readMessage(inputStream: InputStream): BluetoothData.Data<Messages> {
        return try {
            withContext(Dispatchers.IO) {
                val buffer = ByteArray(1024)
                val bytes: Int = inputStream.read(buffer)
                val readMessage = String(buffer, 0, bytes)
                val milliSecondsTime = System.currentTimeMillis()
                val message = Messages(
                    message = readMessage,
                    time = milliSecondsTime,
                    type = Constants.MESSAGE_TYPE_RECEIVED
                )
                BluetoothData.Data(ConnectionState.Connected, message)
            }
        } catch (e: Exception) {
            BluetoothData.Data(ConnectionState.Failed)
        }
    }

    override suspend fun writeMessage(
        buffer: ByteArray,
        outputStream: OutputStream
    ): BluetoothData.Data<Messages> {
        return try {
            withContext(Dispatchers.IO) {
                outputStream.write(buffer)
                val msg = buffer.toCustomString()
                val milliSecondsTime = System.currentTimeMillis()
                val message = Messages(
                    message = msg,
                    time = milliSecondsTime,
                    type = Constants.MESSAGE_TYPE_SENT
                )
                BluetoothData.Data(ConnectionState.Connected, message)
            }
        } catch (e: java.lang.Exception) {
            BluetoothData.Data(ConnectionState.Failed)
        }
    }

    override suspend fun closeTransfer(bluetoothSocket: BluetoothSocket): BluetoothData<ConnectionState> {
        return try {
            bluetoothSocket.close()
            BluetoothData.Data(ConnectionState.Closed)
        } catch (e: java.lang.Exception) {
            BluetoothData.Data(ConnectionState.Failed)
        }
    }

}