package com.hezy.model.usecase.server

import android.bluetooth.BluetoothAdapter
import com.hezy.model.gateway.HandleServerGateway
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ConnectionState
import com.hezy.model.state.ProgressBarState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HandleServerUseCase @Inject constructor(
    private val handleServerGateway: HandleServerGateway,
    private val bluetoothAdapter: BluetoothAdapter,
    private val isSecure: Boolean
) {
    suspend operator fun invoke(): Flow<BluetoothData<ConnectionState>> {

        return flow {
            emit(BluetoothData.Status(progressBarState = ProgressBarState.Loading))
            val initServer = handleServerGateway.initServer(bluetoothAdapter, isSecure)

            if (initServer.data != null) {
                handleServerGateway.connectFrom(initServer.data)
                emit(BluetoothData.Data(ConnectionState.Connected))
            }

            emit(BluetoothData.Status(progressBarState = ProgressBarState.Idel))
        }
    }
}