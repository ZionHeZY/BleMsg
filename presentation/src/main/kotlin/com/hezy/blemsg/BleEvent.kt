package com.hezy.blemsg

import com.hezy.model.state.ProgressBarState

sealed class BleEvent {
    data class ChangeProgressBarStatus(val status: ProgressBarState) : BleEvent()
    data class NotificationReceived(val msg: String) : BleEvent()
}