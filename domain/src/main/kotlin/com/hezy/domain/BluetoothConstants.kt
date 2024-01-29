package com.hezy.domain

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

object BluetoothConstants {
    val UUID_SECURE = UUID.fromString("fc5deb71-9d4b-460b-b725-b06ea79bda5a")
    val UUID_INSECURE = UUID.fromString("d620cd2b-e0a4-435b-b02e-40324d57195b")


    const val NAME_SECURE = "BluetoothChatSecure"
    const val NAME_INSECURE = "BluetoothChatInsecure"

    const val BLUETOOTH_SOCKET_TYPE_SECURE = "Secure"
    const val BLUETOOTH_SOCKET_TYPE_INSECURE = "Insecure"

    val DEFAULT_OUTPUT_STREAM = ByteArrayOutputStream(1024)
    val DEFAULT_INPUT_STREAM = ByteArrayInputStream(ByteArray(1024))

    const val MESSAGE_TYPE_SENT = 0
    const val MESSAGE_TYPE_RECEIVED = 1
}