package com.hezy.model.entity

data class Devices(val deviceName:String,val deviceAddress: String) {
    override fun equals(other: Any?): Boolean {
        val devices = other as Devices
        return deviceAddress == devices.deviceAddress
    }

    override fun hashCode(): Int {
        return deviceAddress.hashCode()
    }
}