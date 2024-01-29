package com.hezy.model.state

sealed class BluetoothConnectionState {
    object None : BluetoothConnectionState() {
        override fun toString(): String {
            return "None"
        }
    }

    object Connected : BluetoothConnectionState() {
        override fun toString(): String {
            return "Connected"
        }
    }
}