package com.hezy.blemsg

import com.hezy.model.Constants.DEFAULT_INPUT_STREAM
import com.hezy.model.Constants.DEFAULT_OUTPUT_STREAM
import com.hezy.model.entity.Messages
import com.hezy.model.state.BluetoothConnectionState
import com.hezy.model.state.ProgressBarState
import java.io.InputStream
import java.io.OutputStream

data class BleState(
    val progressBarState: ProgressBarState = ProgressBarState.Idel,
    val bluetoothConnectionState: BluetoothConnectionState = BluetoothConnectionState.None,
    val messages: List<Messages> = emptyList(),
    val inputStream: InputStream = DEFAULT_INPUT_STREAM,
    val outputStream: OutputStream = DEFAULT_OUTPUT_STREAM
)