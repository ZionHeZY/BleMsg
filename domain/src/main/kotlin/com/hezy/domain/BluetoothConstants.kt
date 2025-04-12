package com.hezy.domain

import java.util.*

/**
 * 蓝牙相关常量，包含 UUID 和连接类型
 */
object BluetoothConstants {
    // 安全和非安全连接的 UUID
    val UUID_SECURE = UUID.fromString("fc5deb71-9d4b-460b-b725-b06ea79bda5a")
    val UUID_INSECURE = UUID.fromString("d620cd2b-e0a4-435b-b02e-40324d57195b")

    // 蓝牙服务名称
    const val NAME_SECURE = "BluetoothChatSecure"
    const val NAME_INSECURE = "BluetoothChatInsecure"

    // 蓝牙连接类型
    const val BLUETOOTH_SOCKET_TYPE_SECURE = "Secure"
    const val BLUETOOTH_SOCKET_TYPE_INSECURE = "Insecure"
}