package com.hezy.model.usecase.message

import android.bluetooth.BluetoothSocket
import com.hezy.model.gateway.HandleMessageGateway
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ConnectionState
import com.hezy.model.state.ProgressBarState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WriteMessageUseCase @Inject constructor(
    private val bluetoothSocket: BluetoothSocket,
    private val handleMessageGateway: HandleMessageGateway
) {
    suspend operator fun invoke(message: ByteArray): Flow<BluetoothData<ConnectionState>> {

        return flow {
            emit(BluetoothData.Status(progressBarState = ProgressBarState.Loading))
            val initServer = handleMessageGateway.initTransferMessage(bluetoothSocket)

            if (initServer.data != null) {
                handleMessageGateway.writeMessage(message, initServer.data.outputStream)
                emit(BluetoothData.Data(ConnectionState.Connected))
            }

            emit(BluetoothData.Status(progressBarState = ProgressBarState.Idel))
        }
    }
}