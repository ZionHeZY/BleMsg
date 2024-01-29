package com.hezy.model.state

sealed class BluetoothData<T> {
    data class Status<T>(val progressBarState: ProgressBarState = ProgressBarState.Idel) :
        BluetoothData<T>()

    data class Data<T>(
        val connectionState: ConnectionState = ConnectionState.None,
        val data: T? = null
    ) : BluetoothData<T>()
}