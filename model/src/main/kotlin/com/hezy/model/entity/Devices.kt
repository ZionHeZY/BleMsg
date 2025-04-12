package com.hezy.model.entity

/**
 * 设备类型
 */
enum class DeviceType {
    PAIRED,    // 已配对设备
    SCANNED    // 扫描到的新设备
}

/**
 * 设备实体类
 */
data class Devices(
    val deviceName: String,
    val deviceAddress: String,
    val deviceType: DeviceType = DeviceType.SCANNED // 默认为扫描到的设备
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is Devices) return false
        return deviceAddress == other.deviceAddress
    }

    override fun hashCode(): Int {
        return deviceAddress.hashCode()
    }
}