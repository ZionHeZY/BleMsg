package com.hezy.model.usecase.message

import android.bluetooth.BluetoothSocket
import com.hezy.model.entity.Messages
import com.hezy.model.gateway.HandleMessageGateway
import com.hezy.model.state.BluetoothData
import com.hezy.model.state.ProgressBarState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReadMessageUseCase @Inject constructor(
    private val bluetoothSocket: BluetoothSocket,
    private val handleMessageGateway: HandleMessageGateway
) {
    suspend operator fun invoke(): Flow<BluetoothData<Messages>> {
        return flow {
            emit(BluetoothData.Status(progressBarState = ProgressBarState.Loading))
            val initTransferMessage = handleMessageGateway.initTransferMessage(bluetoothSocket)
            if (initTransferMessage.data != null) {
                val readMessage =
                    handleMessageGateway.readMessage(initTransferMessage.data.inputStream)
                emit(readMessage)
            }
            emit(BluetoothData.Status(progressBarState = ProgressBarState.Idel))
        }
    }
}