package com.hezy.model.usecase.client

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ConnectionState
import com.hezy.model.state.ProgressBarState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CloseClientInteractor {
    @SuppressLint("MissingPermission")
    fun execute(bluetoothSocket: BluetoothSocket): Flow<BluetoothData<ConnectionState>> = flow {
        try {
            emit(BluetoothData.Status(progressBarState = ProgressBarState.Loading))

            bluetoothSocket.close()
            emit(BluetoothData.Data(ConnectionState.Closed))
        } catch (e: Exception) {
            emit(BluetoothData.Data(ConnectionState.Failed))
        } finally {
            emit(BluetoothData.Status(progressBarState = ProgressBarState.Idel))
        }
    }
}