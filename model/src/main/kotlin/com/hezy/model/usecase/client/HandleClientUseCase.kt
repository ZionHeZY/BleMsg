package com.hezy.model.usecase.client

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.hezy.model.gateway.HandleClientGateway
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ConnectionState
import com.hezy.model.state.ProgressBarState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HandleClientUseCase @Inject constructor(
    private val handleClientGateway: HandleClientGateway,
    private val bluetoothDevice: BluetoothDevice,
    private val bluetoothAdapter: BluetoothAdapter,
    private val isSecure: Boolean
) {
    suspend operator fun invoke(): Flow<BluetoothData<ConnectionState>> = flow {
        emit(BluetoothData.Status(progressBarState = ProgressBarState.Loading))

        val initResult = handleClientGateway.initClient(bluetoothDevice, bluetoothAdapter, isSecure)
        if (initResult.data != null) {
            val connectResult = handleClientGateway.connectTo(initResult.data)
            emit(connectResult)
        }

        emit(BluetoothData.Status(progressBarState = ProgressBarState.Idel))
    }
}
